package vcms.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocalDateRequest {
    private LocalDate selectedDate;
}
