package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.VaccineCreationRequest;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccineResponse;
import vcms.service.VaccineService;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {
    private final VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping("/all")
    public ApiResponse<List<VaccineResponse>> getAllVaccines() {
        return ApiResponse.<List<VaccineResponse>>builder()
                .result(vaccineService.getAllVaccines())
                .build();
    }

    @GetMapping("/detail/{vaccineId}")
    public ApiResponse<VaccineResponse> getVaccineById(@PathVariable(
            "vaccineId") Long vaccineId) {
        return ApiResponse.<VaccineResponse>builder()
                .result(vaccineService.getVaccineById(vaccineId))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<VaccineResponse> createVaccine(@RequestBody VaccineCreationRequest request) {
        return ApiResponse.<VaccineResponse>builder()
                .result(vaccineService.createVaccine(request))
                .build();
    }

    @PutMapping("/update/{vaccineId}")
    public ApiResponse<VaccineResponse> updateVaccineById(
            @PathVariable("vaccineId") Long vaccineId,
            @RequestBody VaccineUpdateRequest request) {
        return ApiResponse.<VaccineResponse>builder()
                .result(vaccineService.updateVaccine(vaccineId, request))
                .build();
    }

    @DeleteMapping("/delete/{vaccineId}")
    public ApiResponse<String> deleteVaccineById(
            @PathVariable("vaccineId") Long vaccineId) {
        vaccineService.deleteVaccine(vaccineId);
        return ApiResponse.<String>builder()
                .result("Vaccine has been deleted")
                .build();
    }
}
