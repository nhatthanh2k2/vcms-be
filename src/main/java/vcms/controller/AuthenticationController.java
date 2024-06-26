package vcms.controller;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.request.AuthenticationRequest;
import vcms.dto.request.IntrospectRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AuthenticationResponse;
import vcms.dto.response.IntrospectResponse;
import vcms.service.AuthenticationService;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .success(true)
                .code(1000)
                .build();
    }
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(
            @RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .code(1000)
                .success(true)
                .build();
    }
}
