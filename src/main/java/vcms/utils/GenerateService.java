package vcms.utils;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GenerateService {
    public String generateRandomNumber() {
        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            int digit = random.nextInt(10);
            randomNumber.append(digit);
        }

        return randomNumber.toString();
    }
}
