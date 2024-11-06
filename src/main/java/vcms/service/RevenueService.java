package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.response.TimePeriodRevenueResponse;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RevenueService {
    private final OrderService orderService;

    private final VaccinationRecordService vaccinationRecordService;

    private final DateService dateService;

    public RevenueService(OrderService orderService, VaccinationRecordService vaccinationRecordService,
                          DateService dateService) {
        this.orderService = orderService;
        this.vaccinationRecordService = vaccinationRecordService;
        this.dateService = dateService;
    }

    public Long calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
        Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);
        return orderRevenue + recordRevenue;
    }

    public List<TimePeriodRevenueResponse> calculateDailyRevenueOfWeek(LocalDate date) {
        LocalDate startOfWeek = dateService.getStartOfWeek(date);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<TimePeriodRevenueResponse> dailyRevenueResponseList = new ArrayList<>();

        for (LocalDate day = startOfWeek; !day.isAfter(endOfWeek); day = day.plusDays(1)) {
            Long orderRevenue = orderService.calculateOrderTotalRevenue(day, day);
            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(day, day);
            dailyRevenueResponseList.add(
                    new TimePeriodRevenueResponse(day.format(formatter), orderRevenue, recordRevenue));
        }

        return dailyRevenueResponseList;
    }

    public List<TimePeriodRevenueResponse> calculateMonthlyRevenueOfYear(int year) {
        List<TimePeriodRevenueResponse> monthRevenueResponseList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);
            monthRevenueResponseList.add(
                    new TimePeriodRevenueResponse(startDate.getMonth().toString(), orderRevenue, recordRevenue));
        }

        return monthRevenueResponseList;
    }

    public List<TimePeriodRevenueResponse> calculateMonthlyRevenueOfQuarter(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        List<TimePeriodRevenueResponse> monthRevenueResponseList = new ArrayList<>();

        for (int month = startMonth; month <= endMonth; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);
            monthRevenueResponseList.add(
                    new TimePeriodRevenueResponse(startDate.format(formatter), orderRevenue, recordRevenue));
        }

        return monthRevenueResponseList;
    }

    public List<TimePeriodRevenueResponse> calculateQuarterlyRevenueOfYear(int year) {
        List<TimePeriodRevenueResponse> quarterlyRevenueList = new ArrayList<>();

        for (int quarter = 1; quarter <= 4; quarter++) {
            int startMonth = (quarter - 1) * 3 + 1;
            LocalDate startDate = LocalDate.of(year, startMonth, 1);
            LocalDate endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());

            Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);

            quarterlyRevenueList.add(
                    new TimePeriodRevenueResponse("Quý " + quarter + " - " + year, orderRevenue, recordRevenue));
        }

        return quarterlyRevenueList;
    }


//    public List<TimePeriodRevenueResponse> calculateDailyRevenueOfWeek(LocalDate date) {
//        LocalDate startOfWeek = dateService.getStartOfWeek(date);
//        LocalDate endOfWeek = startOfWeek.plusDays(6);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//        List<TimePeriodRevenueResponse> dailyRevenueResponseList = new ArrayList<>();
//
//        for (LocalDate day = startOfWeek; !day.isAfter(endOfWeek); day = day.plusDays(1)) {
//            Long dailyRevenue = calculateTotalRevenue(day, day);
//            dailyRevenueResponseList.add(new TimePeriodRevenueResponse(day.format(formatter), dailyRevenue));
//        }
//
//        return dailyRevenueResponseList;
//    }
//
//    public List<TimePeriodRevenueResponse> calculateMonthlyRevenueOfYear(int year) {
//        List<TimePeriodRevenueResponse> monthRevenueResponseList = new ArrayList<>();
//
//        for (int month = 1; month <= 12; month++) {
//            LocalDate startDate = LocalDate.of(year, month, 1);
//            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
//            Long totalRevenue = calculateTotalRevenue(startDate, endDate);
//            monthRevenueResponseList.add(new TimePeriodRevenueResponse(startDate.getMonth().toString(), totalRevenue));
//        }
//
//        return monthRevenueResponseList;
//    }
//
//    public List<TimePeriodRevenueResponse> calculateMonthlyRevenueOfQuarter(int year, int quarter) {
//
//        int startMonth = (quarter - 1) * 3 + 1;
//        int endMonth = startMonth + 2;
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
//        List<TimePeriodRevenueResponse> monthRevenueResponseList = new ArrayList<>();
//
//        for (int month = startMonth; month <= endMonth; month++) {
//            LocalDate startDate = LocalDate.of(year, month, 1);
//            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
//            Long monthlyRevenue = calculateTotalRevenue(startDate, endDate);
//            monthRevenueResponseList.add(new TimePeriodRevenueResponse(startDate.format(formatter), monthlyRevenue));
//        }
//
//        return monthRevenueResponseList;
//    }
//
//    public List<TimePeriodRevenueResponse> calculateQuarterlyRevenueOfYear(int year) {
//        List<TimePeriodRevenueResponse> quarterlyRevenueList = new ArrayList<>();
//
//        for (int quarter = 1; quarter <= 4; quarter++) {
//            int startMonth = (quarter - 1) * 3 + 1;
//            LocalDate startDate = LocalDate.of(year, startMonth, 1);
//            LocalDate endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());
//
//            Long quarterRevenue = calculateTotalRevenue(startDate, endDate);
//
//            quarterlyRevenueList.add(new TimePeriodRevenueResponse("Quý " + quarter + " - " + year, quarterRevenue));
//        }
//
//        return quarterlyRevenueList;
//    }

}
