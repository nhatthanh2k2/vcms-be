package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.request.AppointmentWithCustomerCodeRequest;
import vcms.dto.request.UpdateAppointmentStatusRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AppointmentResponse;
import vcms.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/list/all")
    public ApiResponse<Map<String, Object>> getAllAppointment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {

        return ApiResponse.<Map<String, Object>>builder()
                .result(appointmentService.getAllAppointment(page, size))
                .build();
    }

    @GetMapping("/list/today")
    public ApiResponse<Map<String, Object>> getAllAppointmentListToday(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {

        return ApiResponse.<Map<String, Object>>builder()
                .result(appointmentService.getAllAppointmentListToday(page, size))
                .build();
    }

    @GetMapping("/list/week")
    public ApiResponse<Map<String, Object>> getAllAppointmentListInWeek(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {

        return ApiResponse.<Map<String, Object>>builder()
                .result(appointmentService.getAllAppointmentListThisWeek(page, size))
                .build();
    }

    @GetMapping("/list/injection-date")
    public ApiResponse<List<AppointmentResponse>> getAppointmentListByInjectionDate(
            @RequestParam("selectedDate") String selectedDateStr
    ) {
        LocalDate selectedDate = LocalDate.parse(selectedDateStr);
        return ApiResponse.<List<AppointmentResponse>>builder()
                .result(appointmentService.getAppointmentListByInjectionDate(selectedDate))
                .build();
    }

    @GetMapping("list/canceled-list")
    public ApiResponse<List<AppointmentResponse>> getCanceledAppointmentList() {
        return ApiResponse.<List<AppointmentResponse>>builder()
                .result(appointmentService.getCanceledAppointmentList())
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<AppointmentResponse> createAppointmentWithOutCustomerCode(
            @RequestBody AppointmentCreationRequest request) {
        return ApiResponse.<AppointmentResponse>builder()
                .result(appointmentService.createAppointment(request))
                .build();
    }

    @PostMapping("/create-code")
    public ApiResponse<AppointmentResponse> createAppointmentWithCustomerCode(
            @RequestBody AppointmentWithCustomerCodeRequest request) {
        return ApiResponse.<AppointmentResponse>builder()
                .result(appointmentService.createAppointmentWithCustomerCode(request))
                .build();
    }

    @PutMapping("/update/appointment-status")
    public ApiResponse<String> updateAppointmentStatus(@RequestBody UpdateAppointmentStatusRequest request) {
        return ApiResponse.<String>builder()
                .result(appointmentService.updateAppointmentStatus(request))
                .build();
    }
}
