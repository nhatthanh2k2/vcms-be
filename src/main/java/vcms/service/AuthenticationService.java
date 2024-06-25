package vcms.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vcms.dto.request.AuthenticationRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AuthenticationResponse;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AuthenticationService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public ApiResponse<AuthenticationResponse> authenticate(
            AuthenticationRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        // Tìm kiếm Employee từ username
        Employee employee = employeeRepository.findByEmployeeUsername(
                request.getUsername());


        // Kiểm tra xem Employee có tồn tại hay không
        if (employee == null) {
            // Trường hợp không tìm thấy Employee
            AuthenticationResponse authenticationResponse =
                    new AuthenticationResponse(null);
            apiResponse.setSuccess(false);
            apiResponse.setResult(new ArrayList<>());
            return apiResponse;
        }

        // So sánh mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(),
                                                          employee.getEmployeePassword());

        if (!isAuthenticated) {
            // Trường hợp mật khẩu không chính xác
            AuthenticationResponse authenticationResponse =
                    new AuthenticationResponse(null);
            apiResponse.setSuccess(false);
            apiResponse.setResult(new ArrayList<>());
            return apiResponse;
        }

        // Trường hợp xác thực thành công
        String token = generateToken(request.getUsername());
        AuthenticationResponse authenticationResponse =
                new AuthenticationResponse(employee);
        authenticationResponse.setToken(token);
        apiResponse.setResult(authenticationResponse);
        apiResponse.setSuccess(true);
        return apiResponse;
    }


    public String generateToken(String username) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("dnthanh.dev")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("UserId", "Test JWT")
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
