package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;

    private int orderTotal;

    private String orderPayment;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderDate;

    private String customerCode;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderInjectionDate;

    private String orderCustomerFullName;

    private Gender orderCustomerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderCustomerDob;

    private String orderCustomerEmail;

    private String orderCustomerPhone;

    private String orderCustomerProvince;

    private String orderCustomerDistrict;

    private String orderCustomerWard;
}
