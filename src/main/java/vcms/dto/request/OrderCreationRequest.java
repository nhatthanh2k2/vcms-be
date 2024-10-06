package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationRequest {


    private String orderCustomerFullName;

    private Gender orderCustomerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderCustomerDob;

    private String orderCustomerEmail;

    private String orderCustomerPhone;

    private String orderCustomerProvince;

    private String orderCustomerDistrict;

    private String orderCustomerWard;

    private int orderTotal;

    @Column(name = "order_payment")
    private String orderPayment;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderInjectionDate;

    private List<Long> orderBatchDetailIdList;

    private List<Long> orderVaccinePackageIdList;
}
