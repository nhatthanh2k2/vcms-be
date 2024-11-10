package vcms.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.VaccineBatchCreationRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccineBatchResponse;
import vcms.mapper.VaccineBatchMapper;
import vcms.model.VaccineBatch;
import vcms.service.VaccineBatchService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vaccine-batch")
public class VaccineBatchController {

    private final VaccineBatchService vaccineBatchService;

    private final VaccineBatchMapper vaccineBatchMapper;

    public VaccineBatchController(VaccineBatchService vaccineBatchService,
                                  VaccineBatchMapper vaccineBatchMapper) {
        this.vaccineBatchService = vaccineBatchService;
        this.vaccineBatchMapper = vaccineBatchMapper;
    }

    @GetMapping("/all")
    public ApiResponse<List<VaccineBatchResponse>> getAllVaccineBatch() {
        return ApiResponse.<List<VaccineBatchResponse>>builder()
                .result(vaccineBatchService.getAllVaccineBatch())
                .build();
    }

    @GetMapping("/detail/sample-batch")
    public ApiResponse<List<BatchDetailResponse>> getDetailOfSampleBatch() {
        return ApiResponse.<List<BatchDetailResponse>>builder()
                .result(vaccineBatchService.getDetailOfSampleBatch())
                .build();
    }

    @GetMapping("/detail/{batchId}")
    public ApiResponse<List<BatchDetailResponse>> getDetailsOfBatch(
            @PathVariable("batchId") Long batchId) {
        return ApiResponse.<List<BatchDetailResponse>>builder()
                .result(vaccineBatchService.getDetailsOfBatch(batchId))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<VaccineBatchResponse> addNewBatch(
            @RequestParam("vaccineBatchNumber") String vaccineBatchNumber,
            @RequestParam("batchDetailFile") MultipartFile batchDetailFile) throws IOException {

        VaccineBatchCreationRequest vaccineBatchCreationRequest = new VaccineBatchCreationRequest();
        vaccineBatchCreationRequest.setVaccineBatchNumber(vaccineBatchNumber);
        vaccineBatchCreationRequest.setBatchDetailFile(batchDetailFile);

        VaccineBatch savedBatch = vaccineBatchService.insertVaccineBatch(vaccineBatchCreationRequest);

        return ApiResponse.<VaccineBatchResponse>builder()
                .result(vaccineBatchMapper.toVaccineBatchResponse(savedBatch))
                .build();
    }
}
