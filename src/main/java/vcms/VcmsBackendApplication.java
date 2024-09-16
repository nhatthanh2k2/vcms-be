package vcms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vcms.service.DiseaseService;
import vcms.service.EmployeeService;
import vcms.service.VaccineService;

@SpringBootApplication
public class VcmsBackendApplication {
    private final DiseaseService diseaseService;

    private final VaccineService vaccineService;

    private final EmployeeService employeeService;

    public VcmsBackendApplication(DiseaseService diseaseService,
                                  VaccineService vaccineService,
                                  EmployeeService employeeService) {
        this.diseaseService = diseaseService;
        this.vaccineService = vaccineService;
        this.employeeService = employeeService;
    }

    public static void main(String[] args) {
        SpringApplication.run(VcmsBackendApplication.class, args);
        System.out.println("The server started successfully!");
    }

//    @Bean
//    ApplicationRunner init() {
//        return args -> {
//            diseaseService.initalDiseaseData();
//            vaccineService.initalVaccineData();
//            employeeService.initalEmployeeData();
//            employeeService.updateEmployeeAvatars();
//            diseaseService.updateDiseaseVaccineRelations();
//        };
//    }

}
