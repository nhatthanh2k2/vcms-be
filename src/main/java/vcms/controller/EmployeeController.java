package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.ChangePasswordRequest;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.request.ForgotPasswordRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.EmployeeResponse;
import vcms.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping("/get")
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
                .result(employeeService.getEmployee(employeeId))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<EmployeeResponse> createEmployee(@RequestBody
                                                        EmployeeCreationRequest request) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.createEmployee(request))
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
        if (employeeService.changePassword(request)) {
            return ApiResponse.<String>builder()
                    .result("Password changed successfully")
                    .build();
        }
        return ApiResponse.<String>builder()
                .result("Password changed failed")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody
                                              ForgotPasswordRequest request) {
        if (employeeService.forgotPassword(request)) {
            return ApiResponse.<String>builder()
                    .result("Password reset successfully")
                    .build();
        }
        return ApiResponse.<String>builder()
                .result("Password reset failed")
                .build();
    }
}
