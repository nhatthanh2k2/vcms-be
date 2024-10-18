package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.ScreeningRecordCreationRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.ScreeningRecordResponse;
import vcms.service.ScreeningRecordService;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/list/create-date")
    public ApiResponse<List<ScreeningRecordResponse>> getAllScreeningRecordByCreateDate(
            @RequestParam("createDate") String strCreateDate
    ) {
        LocalDate createDate = LocalDate.parse(strCreateDate);
        return ApiResponse.<List<ScreeningRecordResponse>>builder()
                .result(screeningRecordService.getAllScreeningRecordByCreateDate(createDate))
                .build();
    }
}
