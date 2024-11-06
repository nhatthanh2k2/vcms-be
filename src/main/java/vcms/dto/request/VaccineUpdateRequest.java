package vcms.dto.request;


import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VaccineUpdateRequest {
    private String vaccineName;

    private Set<String> vaccineAgeRange;

    private String vaccineDescription;

    private String vaccineOrigin;

    private String vaccineInjectionRoute;

    private String vaccineContraindication;

    private String vaccineReaction;

    private int vaccineChildDoseCount;

    private int vaccineAdultDoseCount;

    private String vaccineStorage;

    private String vaccineInjectionSchedule;

    private String vaccinePatient;

    private Long diseaseId;
}
