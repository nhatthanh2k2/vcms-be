package vcms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccinationRecordResponse;
import vcms.service.VaccinationRecordService;

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
}
