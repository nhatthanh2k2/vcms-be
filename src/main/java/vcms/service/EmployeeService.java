package vcms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vcms.dto.request.ChangePasswordRequest;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.request.ForgotPasswordRequest;
import vcms.dto.response.EmployeeResponse;
import vcms.enums.Gender;
import vcms.enums.Role;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.EmployeeMapper;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.utils.DateService;

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

    @Value("${upload.avatarFolder}")
    private String UPLOAD_AVATAR_FOLDER;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployeeMapper employeeMapper,
                           DateService dateService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.dateService = dateService;
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
                .map(employeeMapper::toEmployeeResponse).toList();
    }

    public EmployeeResponse getEmployee(Long employeeId) {
        return employeeMapper.toEmployeeResponse(
                employeeRepository.findById(employeeId)
                        .orElseThrow(
                                () -> new AppException(ErrorCode.NOT_EXISTED)));
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

        String numberToString = employee.getEmployeeId().toString();
        Long employeeId = employee.getEmployeeId();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (numberToString.length() < 7) {
            numberToString = String.format("%07d", employeeId);
        }
        String userName = generateUsername(
                employee.getEmployeeFullName()) + numberToString;
        employee.setEmployeeUsername(userName);
        employee.setEmployeePassword(passwordEncoder.encode(numberToString));
        return employeeMapper.toEmployeeResponse(
                employeeRepository.save(employee));
    }

    public String generateUsername(String fullName) {
        String[] parts = fullName.split(" ");

        // Bắt đầu với chữ cái đầu của họ
        StringBuilder result = new StringBuilder(
                parts[0].substring(0, 1).toLowerCase());

        // Lấy chữ cái đầu của tất cả các từ ở giữa (nếu có)
        for (int i = 1; i < parts.length - 1; i++) {
            result.append(parts[i].substring(0, 1).toLowerCase());
        }

        // Thêm phần tên (từ cuối cùng)
        result.append(parts[parts.length - 1].toLowerCase());

        return result.toString();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse updateEmployeeInfo(Long employeeId,
                                               EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        //String avatar = employee.getEmployeeAvatar();
            employeeMapper.updateEmployee(employee, request);

//            if (file != null && !file.isEmpty()) {
//                String fileExtension = getFileExtension(
//                        file.getOriginalFilename());
//                String newFileName =
//                        employee.getEmployeeId().toString() + "." + fileExtension;
//                Path copyLocation = Paths.get(
//                        UPLOAD_AVATAR_FOLDER + File.separator + newFileName);
//                Files.copy(file.getInputStream(), copyLocation,
//                           StandardCopyOption.REPLACE_EXISTING);
//                employee.setEmployeeAvatar(newFileName);
//            }
//            else employee.setEmployeeAvatar(avatar);

            LocalDateTime updateDateTime = dateService.getDateTimeNow();
            employee.setEmployeeUpdateAt(updateDateTime);

        return employeeMapper.toEmployeeResponse(
                employeeRepository.save(employee));

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public boolean changePassword(ChangePasswordRequest request) {
        var employee = employeeRepository.findByEmployeeUsername(
                                request.getEmployeeUsername())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

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
                        () -> new AppException(ErrorCode.NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        employee.setEmployeePassword(
                passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
        return true;
    }

    public void initalEmployeeData() {
        List<EmployeeCreationRequest> employeeCreationRequestList = new ArrayList<>();

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Nguyễn Văn An", Gender.MALE,
                                            LocalDate.of(1985, 5, 12),
                                            "nguyenvana@gmail.com",
                                            "0909123456", "An Giang",
                                            "Long Xuyên", "Mỹ Hòa",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Trần Văn Bình", Gender.MALE,
                                            LocalDate.of(1979, 3, 22),
                                            "tranvanbinh@gmail.com",
                                            "0909345678", "Cần Thơ",
                                            "Ninh Kiều", "An Hòa",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Lê Văn Cường", Gender.MALE,
                                            LocalDate.of(1988, 10, 10),
                                            "levancuong@gmail.com",
                                            "0909456789", "Đồng Tháp",
                                            "Cao Lãnh", "Phường 3",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Phạm Minh Dũng", Gender.MALE,
                                            LocalDate.of(1982, 7, 5),
                                            "phamminhdung@gmail.com",
                                            "0909567890", "Vĩnh Long",
                                            "Long Hồ", "Phú Quới",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Ngô Văn Hải", Gender.MALE,
                                            LocalDate.of(1990, 4, 15),
                                            "ngovanhai@gmail.com", "0909678901",
                                            "Kiên Giang", "Rạch Giá",
                                            "Vĩnh Thanh",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Vũ Văn Hoàng", Gender.MALE,
                                            LocalDate.of(1993, 11, 25),
                                            "vuvanhoang@gmail.com",
                                            "0909789012", "Tiền Giang",
                                            "Mỹ Tho", "Phường 6",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Nguyễn Thị Lan", Gender.FEMALE,
                                            LocalDate.of(1987, 2, 8),
                                            "nguyenthlan@gmail.com",
                                            "0909890123", "Sóc Trăng",
                                            "Sóc Trăng", "Phường 1",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Trần Thị Hương", Gender.FEMALE,
                                            LocalDate.of(1984, 8, 16),
                                            "tranthihuong@gmail.com",
                                            "0909901234", "Bến Tre",
                                            "Châu Thành", "Tân Thạch",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Lê Thị Nhung", Gender.FEMALE,
                                            LocalDate.of(1991, 6, 20),
                                            "lethinung@gmail.com", "0909012345",
                                            "Hậu Giang", "Vị Thanh", "Vị Tân",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Hoàng Thị Mai", Gender.FEMALE,
                                            LocalDate.of(1995, 9, 9),
                                            "hoangthimai@gmail.com",
                                            "0909123456", "Trà Vinh", "Trà Cú",
                                            "Ngãi Xuyên",
                                            "Bác sĩ", Set.of("DOCTOR")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Nguyễn Thị Minh", Gender.FEMALE,
                                            LocalDate.of(1992, 3, 15),
                                            "nguyenthiminh@gmail.com",
                                            "0909345678", "Cà Mau", "Cà Mau",
                                            "Phường 5",
                                            "Điều dưỡng", Set.of("NURSE")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Phạm Thị Tuyết", Gender.FEMALE,
                                            LocalDate.of(1990, 12, 27),
                                            "phamthituyet@gmail.com",
                                            "0909456789", "Vĩnh Long",
                                            "Tam Bình", "Long Phú",
                                            "Điều dưỡng", Set.of("NURSE")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Lê Thị Bích", Gender.FEMALE,
                                            LocalDate.of(1986, 1, 30),
                                            "lethibich@gmail.com", "0909567890",
                                            "Bạc Liêu", "Giá Rai", "Phường 2",
                                            "Điều dưỡng", Set.of("NURSE")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Đặng Thị Lan", Gender.FEMALE,
                                            LocalDate.of(1994, 4, 3),
                                            "dangthilan@gmail.com",
                                            "0909678901", "An Giang",
                                            "Châu Đốc", "Vĩnh Mỹ",
                                            "Đại học", Set.of("RECEPTIONIST")));

        employeeCreationRequestList.add(
                new EmployeeCreationRequest("Phan Thị Hà", Gender.FEMALE,
                                            LocalDate.of(1997, 7, 22),
                                            "phanthiha@gmail.com", "0909789012",
                                            "Cần Thơ", "Ô Môn", "Thới An",
                                            "Đại học", Set.of("RECEPTIONIST")));
        try {
            for (EmployeeCreationRequest request : employeeCreationRequestList) {
                createEmployee(request);
            }
            System.out.println("Employee Data Inserted Successfully!");
        }
        catch (Exception exception) {
            System.out.println("Employee Data Inserted Failed!");
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
