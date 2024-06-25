package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.VaccineCreationRequest;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccineResponse;
import vcms.mapper.VaccineMapper;
import vcms.model.Vaccine;
import vcms.repository.VaccineRepository;
import vcms.utils.DateService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public ApiResponse<List<Vaccine>> getVaccines(){
        ApiResponse<List<Vaccine>> apiResponse = new ApiResponse<>();
        try {
            List<Vaccine> vaccines = vaccineRepository.findAll();
            apiResponse.setResult(vaccines);
            apiResponse.setSuccess(true);
        }
        catch (Exception ex){
            apiResponse.setResult(new ArrayList<>());
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<VaccineResponse> getVaccine(Long id){
        ApiResponse apiResponse = new ApiResponse();
        try {
            Vaccine vaccine = vaccineRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Vaccine Not Found"));
            apiResponse.setResult(vaccineMapper.toVaccineResponse(vaccine));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex){
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<VaccineResponse> createVaccine(VaccineCreationRequest request){
        ApiResponse apiResponse = new ApiResponse();
        try {
            Vaccine vaccine = vaccineMapper.toVaccine(request);
            vaccineRepository.save(vaccine);
            String vaccineCode = "VAC" + vaccine.getVaccineId().toString();
            vaccine.setVaccineCode(vaccineCode);
            LocalDateTime createDateTime = dateService.getDateTimeNow();
            vaccine.setVaccineCreateAt(createDateTime);
            vaccine.setVaccineUpdateAt(createDateTime);
            apiResponse.setResult(vaccineMapper.toVaccineResponse(vaccineRepository.save(vaccine)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex){
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<VaccineResponse> updateVaccine(Long id,
                                                      VaccineUpdateRequest request){
        ApiResponse apiResponse = new ApiResponse();
        try {
            Vaccine vaccine = vaccineRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Vaccine Not Found"));
            vaccineMapper.updateVaccine(vaccine, request);
            LocalDateTime updateDateTime = dateService.getDateTimeNow();
            vaccine.setVaccineUpdateAt(updateDateTime);
            apiResponse.setResult(vaccineMapper.toVaccineResponse(vaccineRepository.save(vaccine)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex){
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<String> deleteVaccine(Long id){
        ApiResponse apiResponse = new ApiResponse();
        try {
            vaccineRepository.deleteById(id);
            apiResponse.setResult("Vaccine deleted successfully");
            apiResponse.setSuccess(true);
        }
        catch (Exception ex){
            apiResponse.setResult("Vaccine deleted failed");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }
}
