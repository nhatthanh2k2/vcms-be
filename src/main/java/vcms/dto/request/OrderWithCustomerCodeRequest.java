package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderWithCustomerCodeRequest {
    private String customerIdentifier;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderInjectionDate;

    private String orderPayment;

    private int orderTotal;

    private List<Long> orderBatchDetailIdList;

    private List<Long> orderVaccinePackageIdList;

}
