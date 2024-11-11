package vcms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.*;
import vcms.dto.response.EmployeeResponse;
import vcms.enums.Gender;
import vcms.enums.Role;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.EmployeeMapper;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.utils.DateService;
import vcms.utils.GenerateService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final DateService dateService;

    private final GenerateService generateService;

    @Value("${upload.avatarFolder}")
    private String UPLOAD_AVATAR_FOLDER;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployeeMapper employeeMapper,
                           DateService dateService, GenerateService generateService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.dateService = dateService;
        this.generateService = generateService;
    }

    public List<EmployeeResponse> getEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toEmployeeResponse).toList();
    }

    public List<EmployeeResponse> getDoctorAndNurse() {
        Set<String> roles = new HashSet<>();
        roles.add(Role.DOCTOR.name());
        roles.add(Role.NURSE.name());
        return employeeRepository.findByRolesIn(roles).stream()
                .map(employeeMapper::toEmployeeResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Long employeeId) {
        return employeeMapper.toEmployeeResponse(
                employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED)));
    }

    public Employee getEmployeeByEmpId(Long empId) {
        return employeeRepository.findById(empId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
    }

    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByEmployeeUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse createEmployee(EmployeeCreationRequest request) {
        try {
            Employee employee = employeeMapper.toEmployee(request);
            LocalDateTime createDateTime = dateService.getDateTimeNow();
            employee.setEmployeeCreateAt(createDateTime);
            employee.setEmployeeUpdateAt(createDateTime);
            employee.setEmployeeAvatar("default-avatar.png");
            Set<String> roles = request.getRoles();
            employee.setRoles(roles);
            employeeRepository.save(employee);
            String numberToString = employee.getEmployeeId().toString();
            Long employeeId = employee.getEmployeeId();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            if (numberToString.length() < 7) {
                numberToString = String.format("%07d", employeeId);
            }
            String userName = generateService.generateUsername(
                    employee.getEmployeeFullName()) + employeeId;
            employee.setEmployeeUsername(userName);
            employee.setEmployeePassword(passwordEncoder.encode(numberToString));
            return employeeMapper.toEmployeeResponse(
                    employeeRepository.save(employee));
        }
        catch (Exception exception) {
            throw new AppException(ErrorCode.CREATE_FAILED);
        }
    }

    @PreAuthorize("#request.employeeUsername == authentication.name || hasRole('ADMIN')")
    public EmployeeResponse updateEmployeeInfo(EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findByEmployeeUsername(request.getEmployeeUsername())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        employeeMapper.updateEmployee(employee, request);
        LocalDateTime updateDateTime = dateService.getDateTimeNow();
        employee.setEmployeeUpdateAt(updateDateTime);
        return employeeMapper.toEmployeeResponse(
                employeeRepository.save(employee));
    }

    public EmployeeResponse updateQualificationAndPosition(UpdateQualificationAndPositionRequest request) {
        Employee employee = employeeRepository.findByEmployeeUsername(request.getEmployeeUsername())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        employeeMapper.updateQualificationAndPosition(employee, request);
        LocalDateTime updateDateTime = dateService.getDateTimeNow();
        employee.setEmployeeUpdateAt(updateDateTime);
        return employeeMapper.toEmployeeResponse(
                employeeRepository.save(employee));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        if (!employee.getScreeningRecordList().isEmpty() || !employee.getVaccinationRecordList().isEmpty()) {
            throw new AppException(ErrorCode.DELETE_FAILED);
        }
        employeeRepository.delete(employee);
    }

    @PreAuthorize("#request.employeeUsername == authentication.name")
    public String changePassword(ChangePasswordRequest request) {
        String employeeUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        var employee = employeeRepository.findByEmployeeUsername(employeeUsername)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isMatch = passwordEncoder.matches(request.getEmployeePassword(),
                                                  employee.getEmployeePassword());
        if (!isMatch) {
            throw new AppException(ErrorCode.PASSWORDS_NOT_MATCH);
        }
        employee.setEmployeePassword(
                passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);

        return "Change password successfully!";

    }

    public String resetPassword(ResetPasswordRequest request) {
        Employee employee = employeeRepository.findByEmployeeEmail(request.getEmployeeEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        employee.setEmployeePassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
        return "Reset Password Successfully!";
    }

    public EmployeeResponse updateAvatar(String employeeUsername, MultipartFile avatarFile) {
        Employee employee = employeeRepository.findByEmployeeUsername(employeeUsername)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        String contentType = avatarFile.getContentType();
        String fileExtension = "";
        if ("image/jpeg".equals(contentType)) {
            fileExtension = ".jpg";
        }
        else if ("image/png".equals(contentType)) {
            fileExtension = ".png";
        }
        else {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }
        String newAvatarFileName = employee.getEmployeeId() + fileExtension;
        Path avatarFolderPath = Paths.get(UPLOAD_AVATAR_FOLDER).toAbsolutePath().normalize();
        String newFilePath = avatarFolderPath.resolve(newAvatarFileName).toString();
        try {
            avatarFile.transferTo(new File(newFilePath));
            employee.setEmployeeAvatar(newAvatarFileName);
            employeeRepository.save(employee);
            return employeeMapper.toEmployeeResponse(employee);
        }
        catch (Exception ex) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }
    }

    public void insertInitialEmployeeData() {
        List<EmployeeCreationRequest> employeeCreationRequestList = new ArrayList<>();

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Nguyễn Văn An", Gender.MALE,
                                            LocalDate.of(1975, 5, 12),
                                            "nguyenvana@gmail.com",
                                            "0909123456", "Tỉnh An Giang",
                                            "Thành phố Long Xuyên", "Phường Mỹ Hòa",
                                            "BS.CKI", "Giám đốc Y khoa",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Trần Văn Bình", Gender.MALE,
                                            LocalDate.of(1989, 3, 22),
                                            "tranvanbinh@gmail.com",
                                            "0909345678", "Thành phố Cần Thơ",
                                            "Quận Ninh Kiều", "Phường An Hòa",
                                            "BS", "Chuyên viên Y khoa",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Lê Văn Cường", Gender.MALE,
                                            LocalDate.of(1990, 10, 10),
                                            "levancuong@gmail.com",
                                            "0909456789", "Tỉnh Đồng Tháp",
                                            "Thành phố Cao Lãnh", "Phường 3",
                                            "BS", "Chuyên viên Y khoa",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Phạm Minh Dũng", Gender.MALE,
                                            LocalDate.of(1980, 7, 5),
                                            "phamminhdung@gmail.com",
                                            "0909567890", "Tỉnh Vĩnh Long",
                                            "Huyện Long Hồ", "Xã Đông Phú",
                                            "BS", "Chuyên viên Y khoa",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Ngô Văn Hải", Gender.MALE,
                                            LocalDate.of(1973, 4, 15),
                                            "ngovanhai@gmail.com", "0909678901",
                                            "Tỉnh Kiên Giang", "Thành phố Rạch Giá",
                                            "Xã Vĩnh Thanh",
                                            "BS", "Phó Giám đốc Y khoa",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Vũ Văn Hoàng", Gender.MALE,
                                            LocalDate.of(1993, 11, 25),
                                            "vuvanhoang@gmail.com",
                                            "0909789012", "Tỉnh Tiền Giang",
                                            "Thành phố Mỹ Tho", "Phường 6",
                                            "BS", "Quản lý Y khoa",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Nguyễn Thị Lan", Gender.FEMALE,
                                            LocalDate.of(1987, 2, 8),
                                            "nguyenthlan@gmail.com",
                                            "0909890123", "Tỉnh Sóc Trăng",
                                            "Thành phố Sóc Trăng", "Phường 1",
                                            "BS", "Bác sĩ khám sàng lọc",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Trần Thị Hương", Gender.FEMALE,
                                            LocalDate.of(1984, 8, 16),
                                            "tranthihuong@gmail.com",
                                            "0909901234", "Tỉnh Bến Tre",
                                            "Huyện Châu Thành", "Xã Tân Thạch",
                                            "BS", "Bác sĩ khám sàng lọc",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Lê Thị Nhung", Gender.FEMALE,
                                            LocalDate.of(1991, 6, 20),
                                            "lethinung@gmail.com", "0909012345",
                                            "Tỉnh Hậu Giang",
                                            "Thành phố Vị Thanh", "Xã Vị Tân",
                                            "BS", "Bác sĩ khám sàng lọc",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Hoàng Thị Mai", Gender.FEMALE,
                                            LocalDate.of(1995, 9, 9),
                                            "hoangthimai@gmail.com",
                                            "0909123456", "Tỉnh Trà Vinh",
                                            "Huyện Trà Cú", "Xã Ngãi Xuyên",
                                            "BS", "Chuyên viên Y khoa",
                                            Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Nguyễn Thị Minh", Gender.FEMALE,
                                            LocalDate.of(1992, 3, 15),
                                            "nguyenthiminh@gmail.com",
                                            "0909345678", "Tỉnh Cà Mau",
                                            "Thành phố Cà Mau", "Phường 5",
                                            "BS", "Giám Đốc Điều Dưỡng",
                                            Set.of("NURSE")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Phạm Thị Tuyết", Gender.FEMALE,
                                            LocalDate.of(1990, 12, 27),
                                            "phamthituyet@gmail.com",
                                            "0909456789", "Tỉnh Vĩnh Long",
                                            "Huyện Tam Bình", "Xã Long Phú",
                                            "ĐD", "Chuyên viên Điều Dưỡng",
                                            Set.of("NURSE")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Lê Thị Bích", Gender.FEMALE,
                                            LocalDate.of(1986, 1, 30),
                                            "lethibich@gmail.com", "0909567890",
                                            "Tỉnh Bạc Liêu", "Thị xã Giá Rai",
                                            "Phường 1",
                                            "ĐD", "Chuyên viên Điều Dưỡng",
                                            Set.of("NURSE")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Đặng Thị Lan", Gender.FEMALE,
                                            LocalDate.of(1994, 4, 3),
                                            "dangthilan@gmail.com",
                                            "0909678901", "Tỉnh An Giang",
                                            "Thành phố Châu Đốc", "Phường Vĩnh Mỹ",
                                            "ĐD", "Nhân viên lễ tân",
                                            Set.of("RECEPTIONIST")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Phan Thị Hà", Gender.FEMALE,
                                            LocalDate.of(1997, 7, 22),
                                            "phanthiha@gmail.com", "0909789012",
                                            "Thành phố Cần Thơ", "Quận Ninh Kiều", "Phường Cái Khế",
                                            "ĐD", "Nhân viên lễ tân",
                                            Set.of("RECEPTIONIST")));


        try {
            for (EmployeeCreationRequest request : employeeCreationRequestList) {
                createEmployee(request);
            }
            System.out.println("Employee Data Inserted Successfully!");
        }
        catch (Exception exception) {
            System.out.println("Employee Data Insertion Failed!");
        }
    }

    @Transactional
    public void updateEmployeeAvatars() {
        for (Long id = 1001L; id <= 1015L; id++) {
            String avatarFilename = id + ".png";
            // Tìm nhân viên theo id
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee != null) {
                employee.setEmployeeAvatar(avatarFilename);
                employeeRepository.save(employee);
            }
        }
    }
}
