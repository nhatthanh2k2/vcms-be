package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.response.InjectionResponse;
import vcms.dto.response.TimePeriodDoseCountResponse;
import vcms.repository.AppointmentRepository;
import vcms.repository.OrderRepository;
import vcms.repository.VaccinationRecordRepository;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InjectionService {
    private final AppointmentRepository appointmentRepository;

    private final OrderRepository orderRepository;

    private final DateService dateService;

    private final VaccinationRecordRepository vaccinationRecordRepository;

    public List<InjectionResponse> countVaccineDoseCountForNextMonth() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Lấy số lượng từ Appointment và Order
        List<Object[]> appointmentVaccineCounts = appointmentRepository.countVaccinesInAppointments(startDate, endDate);
        List<Object[]> appointmentPackageCounts = appointmentRepository.countPackagesInAppointments(startDate, endDate);
        List<Object[]> orderVaccineCounts = orderRepository.countVaccinesInOrders(startDate, endDate);
        List<Object[]> orderPackageCounts = orderRepository.countPackagesInOrders(startDate, endDate);

        // Sử dụng Map để lưu trữ số lượng mũi tiêm theo tên vắc xin hoặc gói tiêm
        Map<String, InjectionResponse> vaccineCountMap = new HashMap<>();

        // Cộng số lượng từ Appointment (Vaccines)
        for (Object[] count : appointmentVaccineCounts) {
            String vaccineName = (String) count[0];
            int quantity = ((Number) count[1]).intValue();
            vaccineCountMap.merge(vaccineName, new InjectionResponse(vaccineName, quantity),
                                  (existing, newValue) -> {
                                      existing.setDoseCount(existing.getDoseCount() + newValue.getDoseCount());
                                      return existing;
                                  });
        }

        // Cộng số lượng từ Appointment (Packages)
        for (Object[] count : appointmentPackageCounts) {
            String packageName = (String) count[0];
            int quantity = ((Number) count[1]).intValue();
            vaccineCountMap.merge(packageName, new InjectionResponse(packageName, quantity),
                                  (existing, newValue) -> {
                                      existing.setDoseCount(existing.getDoseCount() + newValue.getDoseCount());
                                      return existing;
                                  });
        }

        // Cộng số lượng từ Order (Vaccines)
        for (Object[] count : orderVaccineCounts) {
            String vaccineName = (String) count[0];
            int quantity = ((Number) count[1]).intValue();
            vaccineCountMap.merge(vaccineName, new InjectionResponse(vaccineName, quantity),
                                  (existing, newValue) -> {
                                      existing.setDoseCount(existing.getDoseCount() + newValue.getDoseCount());
                                      return existing;
                                  });
        }

        // Cộng số lượng từ Order (Packages)
        for (Object[] count : orderPackageCounts) {
            String packageName = (String) count[0];
            int quantity = ((Number) count[1]).intValue();
            vaccineCountMap.merge(packageName, new InjectionResponse(packageName, quantity),
                                  (existing, newValue) -> {
                                      existing.setDoseCount(existing.getDoseCount() + newValue.getDoseCount());
                                      return existing;
                                  });
        }

        // Trả về danh sách InjectionResponse với tên vắc xin/gói tiêm và số lượng mũi tiêm
        return new ArrayList<>(vaccineCountMap.values());
    }


    public InjectionService(AppointmentRepository appointmentRepository, OrderRepository orderRepository,
                            DateService dateService, VaccinationRecordRepository vaccinationRecordRepository) {
        this.appointmentRepository = appointmentRepository;
        this.orderRepository = orderRepository;
        this.dateService = dateService;
        this.vaccinationRecordRepository = vaccinationRecordRepository;
    }

    public List<TimePeriodDoseCountResponse> calculateDailyDoseCountOfWeek(LocalDate date) {
        LocalDate startOfWeek = dateService.getStartOfWeek(date);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<TimePeriodDoseCountResponse> dailyVaccinationCountList = new ArrayList<>();

        for (LocalDate day = startOfWeek; !day.isAfter(endOfWeek); day = day.plusDays(1)) {
            Long dailyCount = vaccinationRecordRepository.countByVaccinationRecordDate(day);
            dailyVaccinationCountList.add(new TimePeriodDoseCountResponse(day.format(formatter), dailyCount));
        }

        return dailyVaccinationCountList;
    }

    public List<TimePeriodDoseCountResponse> calculateMonthlyDoseCountOfYear(int year) {
        List<TimePeriodDoseCountResponse> monthVaccinationCountList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            Long monthlyCount = vaccinationRecordRepository.countByVaccinationRecordDateBetween(startDate, endDate);
            monthVaccinationCountList.add(
                    new TimePeriodDoseCountResponse(startDate.getMonth().toString(), monthlyCount));
        }

        return monthVaccinationCountList;
    }

    public List<TimePeriodDoseCountResponse> calculateMonthlyDoseCountOfQuarter(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        List<TimePeriodDoseCountResponse> quarterlyVaccinationCountList = new ArrayList<>();

        for (int month = startMonth; month <= endMonth; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            Long monthlyCount = vaccinationRecordRepository.countByVaccinationRecordDateBetween(startDate, endDate);
            quarterlyVaccinationCountList.add(
                    new TimePeriodDoseCountResponse(startDate.format(formatter), monthlyCount));
        }

        return quarterlyVaccinationCountList;
    }

    public List<TimePeriodDoseCountResponse> calculateQuarterlyDoseCountOfYear(int year) {
        List<TimePeriodDoseCountResponse> quarterlyVaccinationCountList = new ArrayList<>();

        for (int quarter = 1; quarter <= 4; quarter++) {
            int startMonth = (quarter - 1) * 3 + 1;
            LocalDate startDate = LocalDate.of(year, startMonth, 1);
            LocalDate endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());
            Long quarterlyCount = vaccinationRecordRepository.countByVaccinationRecordDateBetween(startDate, endDate);
            quarterlyVaccinationCountList.add(
                    new TimePeriodDoseCountResponse("Quý " + quarter + " - " + year, quarterlyCount));
        }

        return quarterlyVaccinationCountList;
    }
}
