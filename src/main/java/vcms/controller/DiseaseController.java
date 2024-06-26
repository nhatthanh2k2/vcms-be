package vcms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vcms.dto.request.DiseaseRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.DiseaseRespone;
import vcms.service.DiseaseService;

import java.util.List;

@RestController
@RequestMapping("/api/disease")
public class DiseaseController {
    @Autowired
    private DiseaseService diseaseService;

    @GetMapping
    public ApiResponse<List<DiseaseRespone>> getAllDiseases() {
        ApiResponse<List<DiseaseRespone>> apiResponse = new ApiResponse<>();
        try {
            apiResponse.setResult(diseaseService.getDiseases());
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<DiseaseRespone> getDiseaseById(
            @PathVariable("id") Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(diseaseService.getDisease(id));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @PostMapping("/create")
    public ApiResponse<DiseaseRespone> createDisease(
            @RequestBody DiseaseRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(diseaseService.createDisease(request));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<DiseaseRespone> updateDisease(
            @PathVariable("id") Long id,
            @RequestBody DiseaseRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(diseaseService.updateDisease(id, request));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }

        return apiResponse;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteDiseaseById(@PathVariable("id") Long id) {
        ApiResponse apiResponse = new ApiResponse();
        if (diseaseService.deleteDisease(id)) {
            apiResponse.setMessage("Disease deleted successfully");
            apiResponse.setSuccess(true);
        }
        else {
            apiResponse.setMessage("Disease deleted failed");
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }
}
