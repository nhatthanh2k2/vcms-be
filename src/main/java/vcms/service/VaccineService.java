package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.VaccineCreationRequest;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.VaccineResponse;
import vcms.mapper.VaccineMapper;
import vcms.model.Vaccine;
import vcms.repository.VaccineRepository;
import vcms.utils.DateService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    private final VaccineMapper vaccineMapper;

    private final DateService dateService;

    public VaccineService(VaccineRepository vaccineRepository,
                          VaccineMapper vaccineMapper, DateService dateService) {
        this.vaccineRepository = vaccineRepository;
        this.vaccineMapper = vaccineMapper;
        this.dateService = dateService;
    }

    public List<Vaccine> getVaccines() {
        return vaccineRepository.findAll();
    }

    public VaccineResponse getVaccine(Long id) {
        return vaccineMapper.toVaccineResponse(
                vaccineRepository.findById(id).orElseThrow(
                        () -> new RuntimeException("Vaccine Not Found")));
    }

    public VaccineResponse createVaccine(VaccineCreationRequest request) {
        Vaccine vaccine = vaccineMapper.toVaccine(request);
        vaccineRepository.save(vaccine);
        String vaccineCode = "VAC" + vaccine.getVaccineId().toString();
        vaccine.setVaccineCode(vaccineCode);
        LocalDateTime createDateTime = dateService.getDateTimeNow();
        vaccine.setVaccineCreateAt(createDateTime);
        vaccine.setVaccineUpdateAt(createDateTime);
        return vaccineMapper.toVaccineResponse(vaccineRepository.save(vaccine));
    }

    public VaccineResponse updateVaccine(Long id,
                                         VaccineUpdateRequest request) {
        Vaccine vaccine = vaccineRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Vaccine Not Found"));
        vaccineMapper.updateVaccine(vaccine, request);
        LocalDateTime updateDateTime = dateService.getDateTimeNow();
        vaccine.setVaccineUpdateAt(updateDateTime);
        return vaccineMapper.toVaccineResponse(vaccineRepository.save(vaccine));
    }

    public boolean deleteVaccine(Long id) {
        try {
            vaccineRepository.deleteById(id);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
}
