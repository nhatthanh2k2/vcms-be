package vcms.mapper;

import org.mapstruct.Mapper;
import vcms.dto.request.VaccinePackageCreationRequest;
import vcms.dto.response.VaccinePackageResponse;
import vcms.model.VaccinePackage;

@Mapper(componentModel = "spring")
public interface VaccinePackageMapper {

    VaccinePackage toVaccinePackage(
            VaccinePackageCreationRequest vaccinePackageCreationRequest);

    VaccinePackageResponse toVaccinePackageResponse(
            VaccinePackage vaccinePackage);
}
