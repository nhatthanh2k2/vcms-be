package vcms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.EmployeeResponse;
import vcms.model.Employee;
import vcms.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

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

    @PutMapping("/update/{id}")
    public ApiResponse<Employee> updateEmployeeById(
            @PathVariable("id") Long id,
            @RequestBody EmployeeUpdateRequest request) {
        return employeeService.updateEmployee(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteEmployeeById(@PathVariable("id") Long id) {
        return employeeService.deleteEmployee(id);
    }
}
