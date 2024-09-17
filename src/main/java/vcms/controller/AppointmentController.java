package vcms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.request.AppointmentWithCustomerCodeRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AppointmentResponse;
import vcms.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }


    @PostMapping("/create")
    public ApiResponse<AppointmentResponse> createAppointmentWithOutCustomerCode(
            @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
        return ApiResponse.<AppointmentResponse>builder()
                .result(appointmentService.createAppointmentWithoutCustomerCode(
                        appointmentCreationRequest))
                .build();
    }

    @PostMapping("/create-code")
    public ApiResponse<AppointmentResponse> createAppointmentWithCustomerCode(
            @RequestBody AppointmentWithCustomerCodeRequest request) {
        return ApiResponse.<AppointmentResponse>builder()
                .result(appointmentService.createAppointmentWithCustomerCode(
                        request))
                .build();
    }
}
