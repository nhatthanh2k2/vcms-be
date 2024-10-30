package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.request.VaccinationRecordCreationRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.TimePeriodDoseCountResponse;
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

    @GetMapping("/count-dose/day-of-week")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateDailyDoseCountOfWeek(
            @RequestParam("date") String strDate
    ) {
        LocalDate date = LocalDate.parse(strDate);
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(vaccinationRecordService.calculateDailyDoseCountOfWeek(date))
                .build();
    }

    @GetMapping("/count-dose/month-of-year")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateMonthlyDoseCountOfYear(
            @RequestParam("year") int year
    ) {
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(vaccinationRecordService.calculateMonthlyDoseCountOfYear(year))
                .build();
    }

    @GetMapping("/count-dose/month-of-quarter")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateMonthlyDoseCountOfQuarter(
            @RequestParam("year") int year,
            @RequestParam("quarter") int quarter
    ) {
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(vaccinationRecordService.calculateMonthlyDoseCountOfQuarter(year, quarter))
                .build();
    }

    @GetMapping("/count-dose/quarter-of-year")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateQuarterlyDoseCountOfYear(
            @RequestParam("year") int year
    ) {
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(vaccinationRecordService.calculateQuarterlyDoseCountOfYear(year))
                .build();
    }
}
