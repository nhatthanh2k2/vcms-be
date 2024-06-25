package vcms.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DateService {

    public LocalDateTime getDateTimeNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd-MM-yyyy HH-mm-ss");
        String strDateTime = now.format(formatter);
        LocalDateTime formattedLocalDateTime =
                LocalDateTime.parse(strDateTime, formatter);
        return formattedLocalDateTime;
    }
}
