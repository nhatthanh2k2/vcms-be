package vcms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vcms.dto.request.DiseaseRequest;
import vcms.dto.response.DiseaseRespone;
import vcms.model.Disease;

@Mapper(componentModel = "spring")
public interface DiseaseMapper {
    Disease toDisease(DiseaseRequest request);

    DiseaseRespone toDiseaseResponse(Disease disease);

    void updateDisease(@MappingTarget Disease disease, DiseaseRequest request);
}
