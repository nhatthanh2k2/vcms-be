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
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AuthenticationResponse;
import vcms.dto.response.IntrospectResponse;
import vcms.enums.Role;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final EmployeeRepository employeeRepository;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public AuthenticationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public ApiResponse<IntrospectResponse> introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        IntrospectResponse introspectResponse = new IntrospectResponse();
        ApiResponse apiResponse = new ApiResponse();
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if (verified && expiryTime.after(new Date())) {
            introspectResponse.setValid(true);
            apiResponse.setResult(introspectResponse);
            apiResponse.setSuccess(true);
        }
        else {
            introspectResponse.setValid(false);
            apiResponse.setResult(introspectResponse);
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }


    public ApiResponse<Object> authenticate(AuthenticationRequest request) {
        ApiResponse<Object> apiResponse = new ApiResponse();
        Optional<Employee> optionalEmployee =
                employeeRepository.findByEmployeeUsername(
                request.getUsername());

        if (optionalEmployee.isEmpty()) {
            AuthenticationResponse authenticationResponse =
                    new AuthenticationResponse();
            apiResponse.setSuccess(false);
            return apiResponse;
        }

        Employee employee = optionalEmployee.get();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(),
                                                          employee.getEmployeePassword());

        if (!isAuthenticated) {
            AuthenticationResponse authenticationResponse =
                    new AuthenticationResponse();
            apiResponse.setSuccess(false);
            return apiResponse;
        }

        String token = generateToken(employee.getEmployeeUsername(),
                                     employee.getEmployeeEmail(),
                                     employee.getEmployeeRole());
        AuthenticationResponse authenticationResponse =
                new AuthenticationResponse();
        authenticationResponse.setToken(token);
        authenticationResponse.setAuthenticated(true);
        apiResponse.setResult(authenticationResponse);
        apiResponse.setSuccess(true);
        return apiResponse;
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
