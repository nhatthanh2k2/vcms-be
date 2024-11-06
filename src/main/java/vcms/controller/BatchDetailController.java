package vcms.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.request.UpdateVaccinePriceRequest;
import vcms.dto.response.ApiResponse;
import vcms.service.BatchDetailService;

@RestController
@RequestMapping("/api/batch-details")
public class BatchDetailController {
    private final BatchDetailService batchDetailService;

    public BatchDetailController(BatchDetailService batchDetailService) {
        this.batchDetailService = batchDetailService;
    }

    @PutMapping("/update-price")
    public ApiResponse<String> updateVaccinePrice(
            @RequestBody UpdateVaccinePriceRequest request
    ) {
        batchDetailService.updateVaccinePrice(request);
        return ApiResponse.<String>builder()
                .result("Vaccine price updated successfully.")
                .build();
    }
}
