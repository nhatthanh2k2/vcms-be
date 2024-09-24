package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LookupCustomerRequest {
    private String lookupCustomerCode;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate lookupCustomerDob;
}
