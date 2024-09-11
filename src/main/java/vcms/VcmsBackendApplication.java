package vcms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import vcms.service.DiseaseService;
import vcms.service.VaccineService;

@SpringBootApplication
public class VcmsBackendApplication {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private VaccineService vaccineService;

    public static void main(String[] args) {
        SpringApplication.run(VcmsBackendApplication.class, args);
        System.out.println("The server started successfully!");
    }

    @Bean
    ApplicationRunner init() {
        return args -> {
            diseaseService.insertDiseaseDataToDB();
            vaccineService.insertVaccineDataToDB();
            diseaseService.updateDiseaseVaccineRelations();
        };
    }

}
