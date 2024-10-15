package vcms.mapper;

import org.mapstruct.Mapper;
import vcms.dto.request.ScreeningRecordCreationRequest;
import vcms.dto.response.ScreeningRecordResponse;
import vcms.model.ScreeningRecord;

@Mapper(componentModel = "spring")
public interface ScreeningRecordMapper {
    ScreeningRecord toScreeningRecord(ScreeningRecordCreationRequest request);

    ScreeningRecordResponse toScreeningRecordResponse(ScreeningRecord record);
}
