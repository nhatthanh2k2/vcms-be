package vcms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vcms.dto.request.VaccineCreationRequest;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.VaccineResponse;
import vcms.model.Vaccine;

@Mapper(componentModel = "spring")
public interface VaccineMapper {
    Vaccine toVaccine(VaccineCreationRequest request);

    VaccineResponse toVaccineResponse(Vaccine vaccine);

    void updateVaccine(@MappingTarget Vaccine vaccine,
                       VaccineUpdateRequest request);
}
