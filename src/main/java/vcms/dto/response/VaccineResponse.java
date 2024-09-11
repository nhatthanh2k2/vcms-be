package vcms.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class VaccineResponse {
    private Long vaccineId;

    private String vaccineCode;

    private String vaccineName;

    private String vaccineImage;

    private String vaccinePurpose;

    private String vaccineOrigin;

    private String vaccineInjectionRoute;

    private String vaccineContraindication;

    private String vaccineReaction;

    private int vaccineChildDoseCount;

    private int vaccineAdultDoseCount;

    private String vaccineStorage;

    private String vaccineInjectionSchedule;

    private String vaccinePatient;

    private LocalDateTime vaccineCreateAt;

    private LocalDateTime vaccineUpdateAt;
}
