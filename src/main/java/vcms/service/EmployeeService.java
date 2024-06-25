package vcms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.EmployeeResponse;
import vcms.enums.Role;
import vcms.mapper.EmployeeMapper;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.utils.DateService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DateService dateService;

    public ApiResponse<List<Employee>> getEmployees() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        try {
            List<Employee> employees = employeeRepository.findAll();
            apiResponse.setResult(employees);
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(new ArrayList<>());
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<Employee> getEmployee(Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(
                            () -> new RuntimeException("Employee Not Found!"));
            apiResponse.setResult(employee);
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<EmployeeResponse> createEmployee(
            EmployeeCreationRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Employee employee = employeeMapper.toEmployee(request);
            LocalDateTime createDateTime = dateService.getDateTimeNow();
            employee.setEmployeeCreateAt(createDateTime);
            employee.setEmployeeUpdateAt(createDateTime);
            employeeRepository.save(employee);
            String numberToString = employee.getEmployeeId().toString();
            char adminChar = 'A', doctorChar = 'D', staffChar = 'S';
            Long id = employee.getEmployeeId();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            if (numberToString.length() < 9) {
                numberToString = String.format("%09d", id);
            }
            if (employee.getEmployeeRole() == Role.ADMIN) {
                employee.setEmployeeUsername(adminChar + numberToString);
                employee.setEmployeePassword(
                        passwordEncoder.encode(adminChar + numberToString));
            }
            if (employee.getEmployeeRole() == Role.DOCTOR) {
                employee.setEmployeeUsername(doctorChar + numberToString);
                employee.setEmployeePassword(
                        passwordEncoder.encode(doctorChar + numberToString));
            }
            if (employee.getEmployeeRole() == Role.STAFF) {
                employee.setEmployeeUsername(staffChar + numberToString);
                employee.setEmployeePassword(
                        passwordEncoder.encode(staffChar + numberToString));
            }

            apiResponse.setResult(employeeMapper.toEmployeeResponse(
                    employeeRepository.save(employee)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<Employee> updateEmployee(Long id,
                                                        EmployeeUpdateRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(
                            () -> new RuntimeException("Employee Not Found"));

            employeeMapper.updateEmployee(employee, request);
            LocalDateTime updateDateTime = dateService.getDateTimeNow();
            employee.setEmployeeUpdateAt(updateDateTime);

            apiResponse.setResult(employeeRepository.save(employee));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<String> deleteEmployee(Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            employeeRepository.deleteById(id);
            apiResponse.setResult("Employee deleted successfully");
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult("Employee deleted failed");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }
}
