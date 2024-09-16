package vcms.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vcms.enums.Gender;
import vcms.enums.Role;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.service.DiseaseService;
import vcms.service.EmployeeService;
import vcms.service.VaccineService;
import vcms.utils.DateService;

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

    public ApplicationInitConfig(DateService dateService,
                                 EmployeeRepository employeeRepository,
                                 DiseaseService diseaseService,
                                 VaccineService vaccineService,
                                 EmployeeService employeeService) {
        this.dateService = dateService;
        this.employeeRepository = employeeRepository;
        this.diseaseService = diseaseService;
        this.vaccineService = vaccineService;
        this.employeeService = employeeService;
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            if (employeeRepository.findByEmployeeUsername("admin").isEmpty()) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                Employee admin = new Employee();
                admin.setEmployeeFullName("Đỗ Nhật Thanh");
                admin.setEmployeeGender(Gender.MALE);
                LocalDate dob = LocalDate.of(2002, 8, 19);
                admin.setEmployeeDob(dob);
                admin.setEmployeeEmail("thanh2002@gmail.com");
                admin.setEmployeePhone("0399686868");
                admin.setEmployeeProvince("Sóc Trăng");
                admin.setEmployeeDistrict("Long Phú");
                admin.setEmployeeWard("Phú Hữu");
                admin.setEmployeeAvatar("admin-avatar.png");
                admin.setEmployeeQualification("Kỹ Sư");
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
                diseaseService.initalDiseaseData();
                vaccineService.initalVaccineData();
                employeeService.initalEmployeeData();
                employeeService.updateEmployeeAvatars();
                diseaseService.updateDiseaseVaccineRelations();
            }

        };
    }
}
