package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.VaccineCreationRequest;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccineResponse;
import vcms.model.Vaccine;
import vcms.service.VaccineService;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {
    private final VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping()
    public ApiResponse<List<Vaccine>> getAllVaccines(){
        ApiResponse<List<Vaccine>> apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(vaccineService.getVaccines());
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<VaccineResponse> getVaccineById(@PathVariable("id") Long id){
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(vaccineService.getVaccine(id));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @PostMapping("/create")
    public ApiResponse<VaccineResponse> createVaccine(@RequestBody VaccineCreationRequest request){
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(vaccineService.createVaccine(request));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<VaccineResponse> updateVaccineById(@PathVariable("id") Long id,
                                                          @RequestBody VaccineUpdateRequest request){
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(vaccineService.updateVaccine(id, request));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteVaccineById(@PathVariable("id") Long id){
        ApiResponse apiResponse = new ApiResponse();
        if (vaccineService.deleteVaccine(id)) {
            apiResponse.setMessage("Vaccine deleted successfully");
            apiResponse.setSuccess(true);
        }
        else {
            apiResponse.setMessage("Vaccine deleted failed");
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }
}
