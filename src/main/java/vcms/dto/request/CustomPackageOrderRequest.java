package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomPackageOrderRequest {
    //thong tin khach hang de tao order
    private String customerIdentifier;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate injectionDate;

    private String payment;

    // thong tin de tao package custom
    private Long vaccinePackageId;

    private List<Long> vaccineIdList;

    private List<Integer> doseCountList;
}
