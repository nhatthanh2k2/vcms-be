package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.request.VaccinationRecordCreationRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccinationRecordResponse;
import vcms.service.VaccinationRecordService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vaccination-record")
public class VaccinationRecordController {
    private final VaccinationRecordService vaccinationRecordService;

    public VaccinationRecordController(VaccinationRecordService vaccinationRecordService) {
        this.vaccinationRecordService = vaccinationRecordService;
    }

    @PostMapping("/getMyHistory")
    public ApiResponse<List<VaccinationRecordResponse>> getAllRecordsOfCustomer(
            @RequestBody LookupCustomerRequest customerRequest) {
        return ApiResponse.<List<VaccinationRecordResponse>>builder().result(
                vaccinationRecordService.getAllRecordOfCustomer(customerRequest)).build();
    }

    @PostMapping("/create")
    public ApiResponse<VaccinationRecordResponse> createVaccinationRecord(
            @RequestBody VaccinationRecordCreationRequest request) {
        return ApiResponse.<VaccinationRecordResponse>builder()
                .result(vaccinationRecordService.createVaccinationRecord(request))
                .build();
    }

    @GetMapping("/list/create-date")
    public ApiResponse<List<VaccinationRecordResponse>> getAllVaccinationRecordByCreateDate(
            @RequestParam("createDate") String strCreateDate
    ) {
        LocalDate createDate = LocalDate.parse(strCreateDate);
        return ApiResponse.<List<VaccinationRecordResponse>>builder()
                .result(vaccinationRecordService.getAllVaccinationRecordByCreateDate(createDate))
                .build();
    }

}
