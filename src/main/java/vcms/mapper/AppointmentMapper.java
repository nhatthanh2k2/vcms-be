package vcms.mapper;


import org.mapstruct.Mapper;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.response.AppointmentResponse;
import vcms.model.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    Appointment toAppointment(AppointmentCreationRequest request);

    AppointmentResponse toAppointmentResponse(Appointment appointment);
}
