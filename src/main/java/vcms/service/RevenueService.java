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
            // Tính doanh thu từ đơn hàng
            Long orderRevenue = orderService.calculateOrderTotalRevenue(day, day);

            // Tính doanh thu từ phiếu tiêm
            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(day, day);

            // Tổng doanh thu
            Long totalRevenue = (orderRevenue != null ? orderRevenue : 0L) + (recordRevenue != null ? recordRevenue : 0L);

            // Tính chi phí từ đơn hàng
            Long orderCost = orderService.calculateOrderTotalCost(day, day);

            // Tính chi phí từ phiếu tiêm
            Long recordCost = vaccinationRecordService.calculateVaccinationRecordTotalCost(day, day);

            // Tổng chi phí
            Long totalCost = (orderCost != null ? orderCost : 0L) + (recordCost != null ? recordCost : 0L);

            // Lợi nhuận = Doanh thu - Chi phí
            Long profit = totalRevenue - totalCost;

            // Thêm vào danh sách kết quả
            dailyRevenueResponseList.add(
                    new TimePeriodRevenueResponse(day.format(formatter), totalRevenue, totalCost, profit)
            );
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
            Long totalRevenue = (orderRevenue != null ? orderRevenue : 0L) + (recordRevenue != null ? recordRevenue : 0L);

            Long orderCost = orderService.calculateOrderTotalCost(startDate, endDate);
            Long recordCost = vaccinationRecordService.calculateVaccinationRecordTotalCost(startDate, endDate);
            Long totalCost = (orderCost != null ? orderCost : 0L) + (recordCost != null ? recordCost : 0L);

            Long profit = totalRevenue - totalCost;

            monthRevenueResponseList.add(new TimePeriodRevenueResponse(
                    startDate.getMonth().toString(), totalRevenue, totalCost, profit
            ));
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
            Long totalRevenue = (orderRevenue != null ? orderRevenue : 0L) + (recordRevenue != null ? recordRevenue : 0L);

            Long orderCost = orderService.calculateOrderTotalCost(startDate, endDate);
            Long recordCost = vaccinationRecordService.calculateVaccinationRecordTotalCost(startDate, endDate);
            Long totalCost = (orderCost != null ? orderCost : 0L) + (recordCost != null ? recordCost : 0L);

            Long profit = totalRevenue - totalCost;

            monthRevenueResponseList.add(new TimePeriodRevenueResponse(
                    startDate.format(formatter), totalRevenue, totalCost, profit
            ));
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
            Long totalRevenue = (orderRevenue != null ? orderRevenue : 0L) + (recordRevenue != null ? recordRevenue : 0L);

            Long orderCost = orderService.calculateOrderTotalCost(startDate, endDate);
            Long recordCost = vaccinationRecordService.calculateVaccinationRecordTotalCost(startDate, endDate);
            Long totalCost = (orderCost != null ? orderCost : 0L) + (recordCost != null ? recordCost : 0L);

            Long profit = totalRevenue - totalCost;

            quarterlyRevenueList.add(new TimePeriodRevenueResponse(
                    "Quý " + quarter + " - " + year, totalRevenue, totalCost, profit
            ));
        }

        return quarterlyRevenueList;
    }

    public List<TimePeriodRevenueResponse> calculateAnnualRevenueForLastFiveYears() {
        List<TimePeriodRevenueResponse> annualRevenueList = new ArrayList<>();
        int currentYear = LocalDate.now().getYear(); // Năm hiện tại
        int startYear = currentYear - 5 + 1;         // Năm cách đây 5 năm

        for (int year = startYear; year <= currentYear; year++) {
            LocalDate startDate = LocalDate.of(year, 1, 1); // Ngày bắt đầu năm
            LocalDate endDate = LocalDate.of(year, 12, 31); // Ngày kết thúc năm

            // Tính doanh thu
            Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);
            Long totalRevenue = (orderRevenue != null ? orderRevenue : 0L) + (recordRevenue != null ? recordRevenue : 0L);

            // Tính chi phí
            Long orderCost = orderService.calculateOrderTotalCost(startDate, endDate);
            Long recordCost = vaccinationRecordService.calculateVaccinationRecordTotalCost(startDate, endDate);
            Long totalCost = (orderCost != null ? orderCost : 0L) + (recordCost != null ? recordCost : 0L);

            // Tính lợi nhuận
            Long profit = totalRevenue - totalCost;

            // Thêm kết quả vào danh sách
            annualRevenueList.add(new TimePeriodRevenueResponse(
                    "Năm " + year, totalRevenue, totalCost, profit
            ));
        }

        return annualRevenueList;
    }


//    public List<TimePeriodRevenueResponse> calculateDailyRevenueOfWeek(LocalDate date) {
//        LocalDate startOfWeek = dateService.getStartOfWeek(date);
//        LocalDate endOfWeek = startOfWeek.plusDays(6);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        List<TimePeriodRevenueResponse> dailyRevenueResponseList = new ArrayList<>();
//
//        for (LocalDate day = startOfWeek; !day.isAfter(endOfWeek); day = day.plusDays(1)) {
//            Long orderRevenue = orderService.calculateOrderTotalRevenue(day, day);
//            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(day, day);
//            dailyRevenueResponseList.add(
//                    new TimePeriodRevenueResponse(day.format(formatter), orderRevenue, recordRevenue));
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
//            Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
//            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);
//            monthRevenueResponseList.add(
//                    new TimePeriodRevenueResponse(startDate.getMonth().toString(), orderRevenue, recordRevenue));
//        }
//
//        return monthRevenueResponseList;
//    }
//
//    public List<TimePeriodRevenueResponse> calculateMonthlyRevenueOfQuarter(int year, int quarter) {
//        int startMonth = (quarter - 1) * 3 + 1;
//        int endMonth = startMonth + 2;
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
//        List<TimePeriodRevenueResponse> monthRevenueResponseList = new ArrayList<>();
//
//        for (int month = startMonth; month <= endMonth; month++) {
//            LocalDate startDate = LocalDate.of(year, month, 1);
//            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
//            Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
//            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);
//            monthRevenueResponseList.add(
//                    new TimePeriodRevenueResponse(startDate.format(formatter), orderRevenue, recordRevenue));
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
//            Long orderRevenue = orderService.calculateOrderTotalRevenue(startDate, endDate);
//            Long recordRevenue = vaccinationRecordService.calculateVaccinationRecordTotalRevenue(startDate, endDate);
//
//            quarterlyRevenueList.add(
//                    new TimePeriodRevenueResponse("Quý " + quarter + " - " + year, orderRevenue, recordRevenue));
//        }
//
//        return quarterlyRevenueList;
//    }
//

}
