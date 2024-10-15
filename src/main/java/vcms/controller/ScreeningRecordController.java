package vcms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.request.ScreeningRecordCreationRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.ScreeningRecordResponse;
import vcms.service.ScreeningRecordService;

@RestController
@RequestMapping("/api/screening-record")
public class ScreeningRecordController {
    private final ScreeningRecordService screeningRecordService;

    public ScreeningRecordController(ScreeningRecordService screeningRecordService) {
        this.screeningRecordService = screeningRecordService;
    }

    @PostMapping("/create")
    public ApiResponse<ScreeningRecordResponse> createScreeningRecord(
            @RequestBody ScreeningRecordCreationRequest request) {
        return ApiResponse.<ScreeningRecordResponse>builder()
                .result(screeningRecordService.createScreeningRecord(request))
                .build();
    }
}
