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
import vcms.dto.response.IntrospectResponse;
import vcms.service.AuthenticationService;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<Object> login(
            @RequestBody AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(
            @RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        return authenticationService.introspect(request);
    }
}
