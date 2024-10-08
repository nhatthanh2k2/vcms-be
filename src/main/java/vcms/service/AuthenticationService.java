package vcms.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vcms.dto.request.AuthenticationRequest;
import vcms.dto.request.IntrospectRequest;
import vcms.dto.request.LogoutRequest;
import vcms.dto.request.RefreshTokenRequest;
import vcms.dto.response.AuthenticationResponse;
import vcms.dto.response.IntrospectResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.EmployeeMapper;
import vcms.model.Employee;
import vcms.model.InvalidatedToken;
import vcms.repository.InvalidatedTokenRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;


@Service
@Slf4j
public class AuthenticationService {

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthenticationService(EmployeeService employeeService,
                                 InvalidatedTokenRepository invalidatedTokenRepository, EmployeeMapper employeeMapper) {

        this.employeeService = employeeService;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.employeeMapper = employeeMapper;
    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        }
        catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        var employee = employeeService.getEmployeeByUsername(request.getUsername());

        boolean authenticated = passwordEncoder
                .matches(request.getPassword(), employee.getEmployeePassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);

        String token = generateToken(employee);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .employeeResponse(employeeMapper.toEmployeeResponse(employee))
                .build();
    }

    public void logOut(
            LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().tokenId(jit).expiryTime(
                            expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        }
        catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(
            RefreshTokenRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().tokenId(jit).expiryTime(
                        expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var employee = employeeService.getEmployeeByUsername(username);
        var token = generateToken(employee);

        return AuthenticationResponse.builder().token(token).authenticated(
                true).build();
    }

    public String generateToken(Employee employee) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(employee.getEmployeeUsername())
                .issuer("dnthanh.dev")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("email", employee.getEmployeeEmail())
                .claim("scope", buildScope(employee))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            log.error("Cannot create token!", e);
            throw new RuntimeException(e);
        }

    }

    private SignedJWT verifyToken(String token,
                                  boolean isRefresh) throws JOSEException,
            ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                                   .toInstant().plus(REFRESHABLE_DURATION,
                                                     ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(Employee employee) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(employee.getRoles()))
            employee.getRoles().forEach(stringJoiner::add);
        return stringJoiner.toString();
    }
}
