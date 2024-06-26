package vcms.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vcms.dto.request.AuthenticationRequest;
import vcms.dto.request.IntrospectRequest;
import vcms.dto.response.AuthenticationResponse;
import vcms.dto.response.IntrospectResponse;
import vcms.enums.Role;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.repository.EmployeeRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Service
public class AuthenticationService {
    private final EmployeeRepository employeeRepository;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public AuthenticationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        var employee =
                employeeRepository.findByEmployeeUsername(request.getUsername())
                        .orElseThrow(() -> new AppException(
                                ErrorCode.EMPLOYEE_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                                                          employee.getEmployeePassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);

        String token = generateToken(employee.getEmployeeUsername(),
                                     employee.getEmployeeEmail(),
                                     employee.getEmployeeRole());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    public String generateToken(String username, String email, Role role) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("dnthanh.dev")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("UserName", username)
                .claim("Email", email)
                .claim("Role", role)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            System.out.println("Cannot create token!");
            throw new RuntimeException(e);
        }

    }
}
