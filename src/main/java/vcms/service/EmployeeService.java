package vcms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.ChangePasswordRequest;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.request.ForgotPasswordRequest;
import vcms.dto.response.EmployeeResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.EmployeeMapper;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.utils.DateService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    @PreAuthorize("hasRole('ADMIN')")
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse getEmployee(Long id) {
        return employeeMapper.toEmployeeResponse(
                employeeRepository.findById(id)
                        .orElseThrow(
                                () -> new AppException(
                                        ErrorCode.EMPLOYEE_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse createEmployee(
            EmployeeCreationRequest request) {

        Employee employee = employeeMapper.toEmployee(request);
        LocalDateTime createDateTime = dateService.getDateTimeNow();
        employee.setEmployeeCreateAt(createDateTime);
        employee.setEmployeeUpdateAt(createDateTime);
        employee.setEmployeeAvatar("default-avatar.png");
        Set<String> roles = request.getRoles();
        employee.setRoles(roles);
        employeeRepository.save(employee);

        // tạo employee code
        String numberToString = employee.getEmployeeId().toString();
        Long id = employee.getEmployeeId();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (numberToString.length() < 9) {
            numberToString = String.format("%09d", id);
        }
        employee.setEmployeeUsername(numberToString);
        employee.setEmployeePassword(passwordEncoder.encode(numberToString));
        return employeeMapper.toEmployeeResponse(
                employeeRepository.save(employee));
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse updateEmployee(Long id, MultipartFile file,
                                           EmployeeUpdateRequest request) throws IOException {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(
                            () -> new AppException(
                                    ErrorCode.EMPLOYEE_NOT_EXISTED));
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

        return employeeMapper.toEmployeeResponse(
                employeeRepository.save(employee));

    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteEmployee(Long id) {
        try {
            employeeRepository.deleteById(id);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public boolean changePassword(ChangePasswordRequest request) {
        var employee =
                employeeRepository.findByEmployeeUsername(
                                request.getEmployeeUsername())
                        .orElseThrow(() -> new AppException(
                                ErrorCode.EMPLOYEE_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isMatch = passwordEncoder.matches(request.getEmployeePassword(),
                                                  employee.getEmployeePassword());

        if (isMatch) {
            employee.setEmployeePassword(
                    passwordEncoder.encode(request.getNewPassword()));
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }

    public boolean forgotPassword(ForgotPasswordRequest request) {

        Employee employee = employeeRepository.findByEmployeeEmail(
                        request.getEmployeeEmail())
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        employee.setEmployeePassword(
                passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
        return true;
    }
}
