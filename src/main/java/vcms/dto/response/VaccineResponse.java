package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

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

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineCreateAt;

    private DiseaseResponse diseaseResponse;

}
