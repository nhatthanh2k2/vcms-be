package vcms.mapper;

import org.mapstruct.Mapper;
import vcms.dto.response.VaccinationRecordResponse;
import vcms.model.VaccinationRecord;

@Mapper(componentModel = "spring")
public interface VaccinationRecordMapper {
    VaccinationRecordResponse toVaccinationRecordResponse(VaccinationRecord record);
}
