package vcms.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vcms.enums.Gender;
import vcms.enums.Role;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.util.HashSet;

@Configuration
@Slf4j
public class ApplicationInitConfig {
    @Autowired
    private DateService dateService;

    @Bean
    ApplicationRunner applicationRunner(EmployeeRepository employeeRepository) {
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
                admin.setEmployeeAvatar("default-avatar.png");
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
            }
        };
    }
}
