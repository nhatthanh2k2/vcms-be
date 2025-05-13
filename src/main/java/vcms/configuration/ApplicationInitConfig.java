package vcms.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.VaccineBatchCreationRequest;
import vcms.enums.Gender;
import vcms.enums.Role;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.service.*;
import vcms.utils.DateService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashSet;

@Configuration
@Slf4j
@Order(1)
public class ApplicationInitConfig {
    private final DateService dateService;

    private final EmployeeRepository employeeRepository;

    private final DiseaseService diseaseService;

    private final VaccineService vaccineService;

    private final EmployeeService employeeService;

    private final VaccinePackageService vaccinePackageService;

    private final VaccineBatchService vaccineBatchService;

    private final CustomerService customerService;


    public ApplicationInitConfig(DateService dateService,
                                 EmployeeRepository employeeRepository,
                                 DiseaseService diseaseService,
                                 VaccineService vaccineService,
                                 EmployeeService employeeService,
                                 VaccinePackageService vaccinePackageService,
                                 VaccineBatchService vaccineBatchService,
                                 CustomerService customerService) {
        this.dateService = dateService;
        this.employeeRepository = employeeRepository;
        this.diseaseService = diseaseService;
        this.vaccineService = vaccineService;
        this.employeeService = employeeService;
        this.vaccinePackageService = vaccinePackageService;
        this.vaccineBatchService = vaccineBatchService;
        this.customerService = customerService;
    }

    public static MultipartFile convertToMultipartFile(File file) throws IOException {
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        return new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }

            @Override
            public String getOriginalFilename() {
                return file.getName();
            }

            @Override
            public String getContentType() {
                return "No file";
            }

            @Override
            public boolean isEmpty() {
                return file.length() == 0;
            }

            @Override
            public long getSize() {
                return file.length();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return java.nio.file.Files.readAllBytes(file.toPath());
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return fileSystemResource.getInputStream();
            }

            @Override
            public void transferTo(File dest) throws IOException {
                java.nio.file.Files.copy(file.toPath(), dest.toPath());
            }
        };
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            VaccineBatchCreationRequest request = new VaccineBatchCreationRequest();
            request.setVaccineBatchNumber("B-00-0000");
            String filePath = "/app/vcms/utils/Batch-00.xlsx";
            File file = new File(filePath);
            MultipartFile multipartFile = convertToMultipartFile(file);
            request.setBatchDetailFile(multipartFile);

            if (employeeRepository.findByEmployeeUsername("admin").isEmpty()) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                Employee admin = new Employee();
                admin.setEmployeeFullName("Đỗ Nhật Thanh");
                admin.setEmployeeGender(Gender.MALE);
                LocalDate dob = LocalDate.of(2002, 8, 19);
                admin.setEmployeeDob(dob);
                admin.setEmployeeEmail("bathanh1980@gmail.com");
                admin.setEmployeePhone("0399686868");
                admin.setEmployeeProvince("Tỉnh Sóc Trăng");
                admin.setEmployeeDistrict("Huyện Long Phú");
                admin.setEmployeeWard("Xã Phú Hữu");
                admin.setEmployeeAvatar("admin-avatar.png");
                admin.setEmployeeQualification("ĐH");
                admin.setEmployeePosition("Quản trị viên");
                HashSet<String> roles = new HashSet<>();
                roles.add(Role.ADMIN.name());
                admin.setRoles(roles);
                admin.setEmployeeUsername("admin");
                admin.setEmployeePassword(passwordEncoder.encode(
                        "244466666"));
                admin.setEmployeeCreateAt(dateService.getDateTimeNow());
                admin.setEmployeeUpdateAt(dateService.getDateTimeNow());
                employeeRepository.save(admin);
                log.warn("Admin user has been created!!!");

                // init data
                diseaseService.insertInitialDiseaseData();
                vaccineService.insertInitialVaccineData();
                vaccineService.updateDiseaseVaccineRelations();
                vaccineBatchService.insertVaccineBatch(request);
                vaccinePackageService.insertInitialVaccinePackageData();
                employeeService.insertInitialEmployeeData();
                customerService.insertInitialCustomerData();
                employeeService.updateEmployeeAvatars();

            }

        };
    }
}
