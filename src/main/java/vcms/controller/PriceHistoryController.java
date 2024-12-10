package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.PriceHistoryRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.PriceHistoryResponse;
import vcms.service.PriceHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/price-histories")
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/list/{vaccineId}")
    public ApiResponse<List<PriceHistoryResponse>> getPriceHistoryOfVaccine(
            @PathVariable("vaccineId") Long vaccineId
    ) {
        return ApiResponse.<List<PriceHistoryResponse>>builder()
                .result(priceHistoryService.getPriceHistoryOfVaccine(vaccineId))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<String> addPriceHistory(
            @RequestBody PriceHistoryRequest request
    ) {
        return ApiResponse.<String>builder()
                .result(priceHistoryService.addPriceHistory(request))
                .build();
    }
}
