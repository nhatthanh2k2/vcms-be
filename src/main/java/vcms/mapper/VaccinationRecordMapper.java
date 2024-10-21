package vcms.mapper;

import org.mapstruct.Mapper;
import vcms.dto.request.VaccinationRecordCreationRequest;
import vcms.dto.response.VaccinationRecordResponse;
import vcms.model.VaccinationRecord;

@Mapper(componentModel = "spring")
public interface VaccinationRecordMapper {
    VaccinationRecord toVaccinationRecord(VaccinationRecordCreationRequest request);

    VaccinationRecordResponse toVaccinationRecordResponse(VaccinationRecord vaccinationRecord);
}
