package vcms.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.VaccineCreationRequestByAdmin;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccineResponse;
import vcms.service.VaccineService;

import java.util.List;
import java.util.Set;

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

    @GetMapping("/list-of-disease/{diseaseId}")
    public ApiResponse<List<VaccineResponse>> getVaccineOfDisease(
            @PathVariable("diseaseId") Long diseaseId) {
        return ApiResponse.<List<VaccineResponse>>builder()
                .result(vaccineService.getVaccineOfDisease(diseaseId))
                .build();
    }

    @GetMapping("/detail/{vaccineId}")
    public ApiResponse<VaccineResponse> getVaccineById(@PathVariable(
            "vaccineId") Long vaccineId) {
        return ApiResponse.<VaccineResponse>builder()
                .result(vaccineService.getVaccineById(vaccineId))
                .build();
    }

    @PostMapping("/create-vaccine")
    public ApiResponse<VaccineResponse> createVaccine(
            @RequestParam("vaccineName") String vaccineName,
            @RequestParam("vaccineOrigin") String vaccineOrigin,
            @RequestParam("vaccineDescription") String vaccineDescription,
            @RequestParam("vaccineContraindication") String vaccineContraindication,
            @RequestParam("vaccineReaction") String vaccineReaction,
            @RequestParam("vaccineStorage") String vaccineStorage,
            @RequestParam("vaccineInjectionRoute") String vaccineInjectionRoute,
            @RequestParam("vaccineInjectionSchedule") String vaccineInjectionSchedule,
            @RequestParam("vaccinePatient") String vaccinePatient,
            @RequestParam("vaccineAdultDoseCount") int vaccineAdultDoseCount,
            @RequestParam("vaccineChildDoseCount") int vaccineChildDoseCount,
            @RequestParam(value = "vaccineAgeRange") Set<String> vaccineAgeRange,
            @RequestParam(value = "vaccineImage", required = false) MultipartFile vaccineImageFile,
            @RequestParam("diseaseId") Long diseaseId
    ) {
        VaccineCreationRequestByAdmin requestByAdmin =
                new VaccineCreationRequestByAdmin(vaccineName, vaccineImageFile, vaccineAgeRange,
                                                  vaccineDescription, vaccineOrigin, vaccineInjectionRoute,
                                                  vaccineContraindication, vaccineReaction,
                                                  vaccineChildDoseCount, vaccineAdultDoseCount, vaccineStorage
                        , vaccineInjectionSchedule, vaccinePatient, diseaseId);
        return ApiResponse.<VaccineResponse>builder()
                .result(vaccineService.createVaccine(requestByAdmin))
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
