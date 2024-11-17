package vcms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.TimePeriodRevenueResponse;
import vcms.service.RevenueService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {
    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/week/day-of-week")
    public ApiResponse<List<TimePeriodRevenueResponse>> calculateDailyRevenueOfWeek(
            @RequestParam("date") String strDate) {
        LocalDate date = LocalDate.parse(strDate);
        return ApiResponse.<List<TimePeriodRevenueResponse>>builder()
                .result(revenueService.calculateDailyRevenueOfWeek(date))
                .build();
    }

    @GetMapping("/quarter/month-of-quarter")
    public ApiResponse<List<TimePeriodRevenueResponse>> calculateMonthlyRevenueOfQuarter(
            @RequestParam("quarter") int quarter,
            @RequestParam("year") int year) {
        return ApiResponse.<List<TimePeriodRevenueResponse>>builder()
                .result(revenueService.calculateMonthlyRevenueOfQuarter(year, quarter))
                .build();
    }

    @GetMapping("/year/quarter-of-year")
    public ApiResponse<List<TimePeriodRevenueResponse>> calculateQuarterlyRevenueOfYear(
            @RequestParam("year") int year) {
        return ApiResponse.<List<TimePeriodRevenueResponse>>builder()
                .result(revenueService.calculateQuarterlyRevenueOfYear(year))
                .build();
    }

    @GetMapping("/year/month-of-year")
    public ApiResponse<List<TimePeriodRevenueResponse>> calculateMonthlyRevenueOfYear(@RequestParam("year") int year) {
        return ApiResponse.<List<TimePeriodRevenueResponse>>builder()
                .result(revenueService.calculateMonthlyRevenueOfYear(year))
                .build();
    }

    @GetMapping("/year/last-five-years")
    public ApiResponse<List<TimePeriodRevenueResponse>> calculateAnnualRevenueForLastFiveYears() {
        return ApiResponse.<List<TimePeriodRevenueResponse>>builder()
                .result(revenueService.calculateAnnualRevenueForLastFiveYears())
                .build();
    }

}
