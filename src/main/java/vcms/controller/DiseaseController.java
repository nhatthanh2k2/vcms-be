package vcms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vcms.dto.request.DiseaseRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.DiseaseRespone;
import vcms.model.Disease;
import vcms.service.DiseaseService;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {
    @Autowired
    private DiseaseService diseaseService;

    @GetMapping
    public ApiResponse<List<Disease>> getAllDiseases() {
        return diseaseService.getDiseases();
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<DiseaseRespone> getDiseaseById(
            @PathVariable("id") Long id) {
        return diseaseService.getDisease(id);
    }

    @PostMapping("/create")
    public ApiResponse<DiseaseRespone> createDisease(
            @RequestBody DiseaseRequest request) {
        return diseaseService.createDisease(request);
    }

    @PutMapping("/update/{id}")
    public ApiResponse<DiseaseRespone> updateDisease(
            @PathVariable("id") Long id,
            @RequestBody DiseaseRequest request) {
        return diseaseService.updateDisease(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteDiseaseById(@PathVariable("id") Long id) {
        return diseaseService.deleteDisease(id);
    }
}
