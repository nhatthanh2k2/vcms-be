package vcms.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DateService {

    public LocalDateTime getDateTimeNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd-MM-yyyy HH-mm-ss");
        String strDateTime = now.format(formatter);
        return LocalDateTime.parse(strDateTime, formatter);
    }

    public LocalDate getDateNow() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String strDate = now.format(formatter);
        return LocalDate.parse(strDate, formatter);
    }
}
