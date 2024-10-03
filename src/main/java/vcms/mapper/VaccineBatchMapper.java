package vcms.mapper;

import org.mapstruct.Mapper;
import vcms.dto.request.VaccineBatchCreationRequest;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccineBatchResponse;
import vcms.model.BatchDetail;
import vcms.model.VaccineBatch;

@Mapper(componentModel = "spring")
public interface VaccineBatchMapper {

    VaccineBatchResponse toVaccineBatchResponse(VaccineBatch vaccineBatch);

    BatchDetailResponse toBatchDetailResponse(BatchDetail batchDetail);

    VaccineBatch toVaccineBatch(VaccineBatchCreationRequest request);
}
