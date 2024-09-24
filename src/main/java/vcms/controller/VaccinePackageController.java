package vcms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccinePackageDetailResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.service.VaccinePackageService;

import java.util.List;

@RestController
@RequestMapping("/api/vaccine-package")
public class VaccinePackageController {
    private final VaccinePackageService vaccinePackageService;

    public VaccinePackageController(
            VaccinePackageService vaccinePackageService) {
        this.vaccinePackageService = vaccinePackageService;
    }

    @GetMapping("/all")
    public ApiResponse<List<VaccinePackageResponse>> getAllPackage() {
        return ApiResponse.<List<VaccinePackageResponse>>builder()
                .result(vaccinePackageService.getAllVaccinePackage())
                .build();
    }

    @GetMapping("/detail/{packageId}")
    public ApiResponse<List<VaccinePackageDetailResponse>> getDetailsOfPackage(
            @PathVariable("packageId") Long packageId) {
        return ApiResponse.<List<VaccinePackageDetailResponse>>builder()
                .result(vaccinePackageService.getDetailsOfPackage(packageId))
                .build();
    }
}
