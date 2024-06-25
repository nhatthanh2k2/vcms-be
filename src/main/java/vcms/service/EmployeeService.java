package vcms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.ChangePasswordRequest;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.request.ForgotPasswordRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.EmployeeResponse;
import vcms.enums.Role;
import vcms.mapper.EmployeeMapper;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.utils.DateService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final DateService dateService;

    @Value("${upload.avatarFolder}")
    private String UPLOAD_AVATAR_FOLDER;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployeeMapper employeeMapper,
                           DateService dateService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.dateService = dateService;
    }

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
            employee.setEmployeeAvatar("default-avatar.png");
            employeeRepository.save(employee);
            // tạo employee code
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
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public ApiResponse<EmployeeResponse> updateEmployee(Long id,
                                                        MultipartFile file,
                                                        EmployeeUpdateRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(
                            () -> new RuntimeException("Employee Not Found"));
            String avatar = employee.getEmployeeAvatar();
            employeeMapper.updateEmployee(employee, request);

            if (file != null && !file.isEmpty()) {
                String fileExtension = getFileExtension(
                        file.getOriginalFilename());
                String newFileName =
                        employee.getEmployeeId().toString() + "." + fileExtension;
                Path copyLocation = Paths.get(
                        UPLOAD_AVATAR_FOLDER + File.separator + newFileName);
                Files.copy(file.getInputStream(), copyLocation,
                           StandardCopyOption.REPLACE_EXISTING);
                employee.setEmployeeAvatar(newFileName);
            }
            else employee.setEmployeeAvatar(avatar);

            LocalDateTime updateDateTime = dateService.getDateTimeNow();
            employee.setEmployeeUpdateAt(updateDateTime);

            apiResponse.setResult(employeeMapper.toEmployeeResponse(
                    employeeRepository.save(employee)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
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

    public ApiResponse<String> changePassword(ChangePasswordRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        Optional<Employee> optionalEmployee =
                employeeRepository.findByEmployeeUsername(
                        request.getEmployeeUsername());

        if (optionalEmployee.isEmpty()) {
            apiResponse.setResult("Username invalid");
            apiResponse.setSuccess(false);
            return apiResponse;
        }

        Employee employee = optionalEmployee.get();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isMatch = passwordEncoder.matches(request.getEmployeePassword(),
                                                  employee.getEmployeePassword());

        if (isMatch) {
            employee.setEmployeePassword(
                    passwordEncoder.encode(request.getNewPassword()));
            employeeRepository.save(employee);
            apiResponse.setResult("Password change successfully");
            apiResponse.setSuccess(true);
        }
        else {
            apiResponse.setResult("Password invalid");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        Optional<Employee> optionalEmployee =
                employeeRepository.findByEmployeeEmail(
                        request.getEmployeeEmail());

        if (optionalEmployee.isEmpty()) {
            apiResponse.setResult("Email invalid");
            apiResponse.setSuccess(false);
            return apiResponse;
        }
        Employee employee = optionalEmployee.get();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        employee.setEmployeePassword(
                passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
        apiResponse.setResult("Password reset successfully");
        apiResponse.setSuccess(true);
        return apiResponse;
    }
}
