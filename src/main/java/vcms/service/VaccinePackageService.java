package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.response.VaccinePackageResponse;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.VaccinePackage;
import vcms.repository.VaccinePackageRepository;
import vcms.utils.DateService;

import java.util.List;

@Service
public class VaccinePackageService {

    private final VaccinePackageRepository vaccinePackageRepository;

    private final VaccinePackageMapper vaccinePackageMapper;

    private final DateService dateService;

    public VaccinePackageService(
            VaccinePackageRepository vaccinePackageRepository,
            VaccinePackageMapper vaccinePackageMapper,
            DateService dateService) {
        this.vaccinePackageRepository = vaccinePackageRepository;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.dateService = dateService;
    }

    public List<VaccinePackage> getAllVaccinePackage() {
        return vaccinePackageRepository.findAll();
    }

    public VaccinePackageResponse getVaccinePackageById(Long vaccinePackageId) {
        return vaccinePackageMapper.toVaccinePackageResponse(
                vaccinePackageRepository.findById(vaccinePackageId)
                        .orElseThrow(() -> new RuntimeException(
                                "Vaccine Package Not Found!")));
    }

//    public VaccinePackageResponse createVaccinePackage(
//            VaccinePackageCreationRequest vaccinePackageCreationRequest){
//
//    }

}
