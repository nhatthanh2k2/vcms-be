package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.request.AppointmentWithCustomerCodeRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AppointmentResponse;
import vcms.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/list")
    public ApiResponse<List<AppointmentResponse>> getAppointmentListByDate(
            @RequestParam("selectedDate") String selectedDateStr
    ) {
        LocalDate selectedDate = LocalDate.parse(selectedDateStr);
        return ApiResponse.<List<AppointmentResponse>>builder()
                .result(appointmentService.getAppointmentListByDate(
                        selectedDate))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<AppointmentResponse> createAppointmentWithOutCustomerCode(
            @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
        return ApiResponse.<AppointmentResponse>builder()
                .result(appointmentService.createAppointment(
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
