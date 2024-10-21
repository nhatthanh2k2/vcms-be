package vcms.utils;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class GenerateService {
    public String generateRandomNumber() {
        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            randomNumber.append(digit);
        }

        return randomNumber.toString();
    }

    public String generateUsername(String fullName) {
        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}+");
        String noDiacritics = pattern.matcher(normalized).replaceAll("");

        StringBuilder result = new StringBuilder();

        noDiacritics = noDiacritics.replace('Đ', 'd').replace('đ', 'd');

        String[] parts = noDiacritics.trim().split("\\s+");

        if (parts.length > 0) {

            result.append(parts[0].substring(0, 1).toLowerCase());
        }

        for (int i = 1; i < parts.length - 1; i++) {
            result.append(parts[i].substring(0, 1).toLowerCase());
        }

        if (parts.length > 0) {
            result.append(parts[parts.length - 1].toLowerCase());
        }

        return result.toString();
    }
}
