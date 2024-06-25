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
        return employeeService.getEmployees();
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<Employee> getEmployeeById(@PathVariable("id") Long id) {
        return employeeService.getEmployee(id);
    }

    @PostMapping("/create")
    public ApiResponse<EmployeeResponse> createEmployee(@RequestBody
                                                        EmployeeCreationRequest request) {
        return employeeService.createEmployee(request);
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
        return employeeService.updateEmployee(id, file, request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteEmployeeById(@PathVariable("id") Long id) {
        return employeeService.deleteEmployee(id);
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody
                                              ChangePasswordRequest request) {
        return employeeService.changePassword(request);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody
                                              ForgotPasswordRequest request) {
        return employeeService.forgotPassword(request);
    }
}
