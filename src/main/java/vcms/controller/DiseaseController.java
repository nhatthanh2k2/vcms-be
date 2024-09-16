package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.DiseaseRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.DiseaseResponse;
import vcms.service.DiseaseService;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {
    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @GetMapping("/get")
    public ApiResponse<List<DiseaseResponse>> getAllDiseases() {
        return ApiResponse.<List<DiseaseResponse>>builder()
                .result(diseaseService.getDiseases())
                .build();
    }

    @GetMapping("/detail/{diseaseId}")
    public ApiResponse<DiseaseResponse> getDiseaseById(
            @PathVariable("diseaseId") Long diseaseId) {
        return ApiResponse.<DiseaseResponse>builder()
                .result(diseaseService.getDisease(diseaseId))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<DiseaseResponse> createDisease(
            @RequestBody DiseaseRequest request) {
        return ApiResponse.<DiseaseResponse>builder()
                .result(diseaseService.createDisease(request))
                .build();
    }

    @PutMapping("/update/{diseaseId}")
    public ApiResponse<DiseaseResponse> updateDisease(
            @PathVariable("diseaseId") Long diseaseId,
            @RequestBody DiseaseRequest request) {
        return ApiResponse.<DiseaseResponse>builder()
                .result(diseaseService.updateDisease(diseaseId, request))
                .build();
    }

    @DeleteMapping("/delete/{diseaseId}")
    public ApiResponse<String> deleteDiseaseById(
            @PathVariable("diseaseId") Long diseaseId) {
        diseaseService.deleteDisease(diseaseId);
        return ApiResponse.<String>builder()
                .result("Disease has been deleted")
                .build();
    }
}
