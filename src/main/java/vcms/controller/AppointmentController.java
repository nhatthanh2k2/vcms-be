package vcms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AppointmentResponse;
import vcms.mapper.AppointmentMapper;
import vcms.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentMapper appointmentMapper;


    @PostMapping("/create")
    public ApiResponse<AppointmentResponse> createAppointmentWithOutCustomerCode(
            @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
        return ApiResponse.<AppointmentResponse>builder()
                .result(appointmentService.createAppointmentWithoutCustomerCode(
                        appointmentCreationRequest))
                .build();
    }
}
