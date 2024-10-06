package vcms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VcmsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VcmsBackendApplication.class, args);
        System.out.println("The server started successfully!");
    }


}
