package vcms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.ChangePasswordRequest;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.request.ForgotPasswordRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.EmployeeResponse;
import vcms.model.Employee;
import vcms.service.EmployeeService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ApiResponse<List<Employee>> getAllEmployees() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        try {
            apiResponse.setResult(employeeService.getEmployees());
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<EmployeeResponse> getEmployeeById(
            @PathVariable("id") Long id) {
        ApiResponse apiResponse = new ApiResponse<>();
        try {
            apiResponse.setResult(employeeService.getEmployee(id));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @PostMapping("/create")
    public ApiResponse<EmployeeResponse> createEmployee(@RequestBody
                                                        EmployeeCreationRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        try {
            apiResponse.setResult(employeeService.createEmployee(request));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

//    @PostMapping("/create")
//    public ApiResponse<EmployeeResponse> createEmployee(
//            @RequestPart("employeeAvatar") MultipartFile file,
//            @RequestPart("employeeCreationRequest") EmployeeCreationRequest request) {
//        return employeeService.createEmployee(file, request);
//    }

//    @PutMapping("/update/{id}")
//    public ApiResponse<Employee> updateEmployeeById(
//            @PathVariable("id") Long id,
//            @RequestBody EmployeeUpdateRequest request) {
//        return employeeService.updateEmployee(id, request);
//    }

    @PutMapping("/update/{id}")
    public ApiResponse<EmployeeResponse> updateEmployeeById(
            @PathVariable("id") Long id,
            @RequestPart("employeeAvatar") MultipartFile file,
            @RequestPart("employeeUpdateRequest") EmployeeUpdateRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(
                    employeeService.updateEmployee(id, file, request));
            apiResponse.setSuccess(true);

        }
        catch (IOException e) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteEmployeeById(@PathVariable("id") Long id) {
        ApiResponse apiResponse = new ApiResponse();
        if (employeeService.deleteEmployee(id)) {
            apiResponse.setMessage("Employee deleted successfully");
            apiResponse.setSuccess(true);
        }
        else {
            apiResponse.setMessage("Employee deleted failed");
            apiResponse.setSuccess(false);
        }

        return apiResponse;
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody
                                              ChangePasswordRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        if (employeeService.changePassword(request)) {
            apiResponse.setMessage("Password changed successfully");
            apiResponse.setSuccess(true);
        }
        else {
            apiResponse.setMessage("Password changed failed");
            apiResponse.setSuccess(false);
        }

        return apiResponse;
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody
                                              ForgotPasswordRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        if (employeeService.forgotPassword(request)) {
            apiResponse.setMessage("Password reset successfully");
            apiResponse.setSuccess(true);
        }
        else {
            apiResponse.setMessage("Password reset failed");
            apiResponse.setSuccess(false);
        }

        return apiResponse;
    }
}
