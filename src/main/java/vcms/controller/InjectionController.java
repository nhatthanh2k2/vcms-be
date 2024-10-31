package vcms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.InjectionResponse;
import vcms.dto.response.TimePeriodDoseCountResponse;
import vcms.service.InjectionService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/injections")
public class InjectionController {

    private final InjectionService injectionService;

    public InjectionController(InjectionService injectionService) {
        this.injectionService = injectionService;
    }

    @GetMapping("count-dose/next-month")
    public ApiResponse<List<InjectionResponse>> countVaccineDoseCountForNextMonth() {
        return ApiResponse.<List<InjectionResponse>>builder()
                .result(injectionService.countVaccineDoseCountForNextMonth())
                .build();
    }

    @GetMapping("/count-dose/day-of-week")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateDailyDoseCountOfWeek(
            @RequestParam("date") String strDate
    ) {
        LocalDate date = LocalDate.parse(strDate);
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(injectionService.calculateDailyDoseCountOfWeek(date))
                .build();
    }

    @GetMapping("/count-dose/month-of-year")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateMonthlyDoseCountOfYear(
            @RequestParam("year") int year
    ) {
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(injectionService.calculateMonthlyDoseCountOfYear(year))
                .build();
    }

    @GetMapping("/count-dose/month-of-quarter")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateMonthlyDoseCountOfQuarter(
            @RequestParam("year") int year,
            @RequestParam("quarter") int quarter
    ) {
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(injectionService.calculateMonthlyDoseCountOfQuarter(year, quarter))
                .build();
    }

    @GetMapping("/count-dose/quarter-of-year")
    public ApiResponse<List<TimePeriodDoseCountResponse>> calculateQuarterlyDoseCountOfYear(
            @RequestParam("year") int year
    ) {
        return ApiResponse.<List<TimePeriodDoseCountResponse>>builder()
                .result(injectionService.calculateQuarterlyDoseCountOfYear(year))
                .build();
    }
}
