package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.*;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.AppointmentResponse;
import vcms.dto.response.EmployeeResponse;
import vcms.dto.response.OrderResponse;
import vcms.service.AppointmentService;
import vcms.service.EmployeeService;
import vcms.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    private final AppointmentService appointmentService;

    private final OrderService orderService;

    public EmployeeController(EmployeeService employeeService, AppointmentService appointmentService,
                              OrderService orderService) {
        this.employeeService = employeeService;
        this.appointmentService = appointmentService;
        this.orderService = orderService;
    }


    @GetMapping("/all")
    public ApiResponse<List<EmployeeResponse>> getAllEmployees() {
        return ApiResponse.<List<EmployeeResponse>>builder()
                .result(employeeService.getEmployees())
                .build();
    }

    @GetMapping("/getDoctorAndNurse")
    public ApiResponse<List<EmployeeResponse>> getAllDoctorAndNurse() {
        return ApiResponse.<List<EmployeeResponse>>builder()
                .result(employeeService.getDoctorAndNurse())
                .build();
    }

    @GetMapping("/detail/{employeeId}")
    public ApiResponse<EmployeeResponse> getEmployeeById(
            @PathVariable("employeeId") Long employeeId) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.getEmployeeById(employeeId))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<EmployeeResponse> createEmployee(@RequestBody
                                                        EmployeeCreationRequest request) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.createEmployee(request))
                .build();
    }

    @PostMapping("/book/vaccination")
    public ApiResponse<?> bookVaccineByEmployee(@RequestBody BookVaccinationRequest request) {
        if (request.getActionType().equalsIgnoreCase("APPT")) {
            return ApiResponse.<AppointmentResponse>builder()
                    .result(appointmentService.createAppointmentFromEmployee(request))
                    .build();
        }
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrderFromEmployee(request))
                .build();
    }

    @PostMapping("/book/custom-package")
    public ApiResponse<?> bookCustomPackage(@RequestBody CustomPackageOrderRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createCustomPackageOrder(request))
                .build();
    }

    @PutMapping("/update/{employeeId}")
    public ApiResponse<EmployeeResponse> updateEmployeeInfo(
            @PathVariable("employeeId") Long employeeId,
            @RequestBody EmployeeUpdateRequest request) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.updateEmployeeInfo(employeeId, request))
                .build();
    }

    @DeleteMapping("/delete/{employeeId}")
    public ApiResponse<String> deleteEmployeeById(
            @PathVariable("employeeId") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ApiResponse.<String>builder()
                .result("Employee has been deleted")
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody
                                              ChangePasswordRequest request) {
        return ApiResponse.<String>builder()
                .result(employeeService.changePassword(request))
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> forgotPassword(@RequestBody
                                              ResetPasswordRequest request) {
        return ApiResponse.<String>builder()
                .result(employeeService.resetPassword(request))
                .build();
    }
}
