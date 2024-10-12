package vcms.dto.request;

import lombok.*;
import vcms.enums.AppointmentStatus;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentStatusRequest {
    private Long appointmentId;

    private AppointmentStatus appointmentStatus;
}
