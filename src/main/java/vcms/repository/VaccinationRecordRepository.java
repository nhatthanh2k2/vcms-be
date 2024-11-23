package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vcms.model.Customer;
import vcms.model.VaccinationRecord;

import java.time.LocalDate;
import java.util.List;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findAllByCustomer(Customer customer);

    boolean existsByVaccinationRecordCode(String code);

    List<VaccinationRecord> findAllByVaccinationRecordDate(LocalDate date);

    Long countByVaccinationRecordDate(LocalDate date);

    Long countByVaccinationRecordDateBetween(LocalDate startDate, LocalDate endDate);

    List<VaccinationRecord> findAllByVaccinationRecordDateBetweenAndAndVaccinationRecordReceiptSource(
            LocalDate startDate, LocalDate endDate, String source);

    @Query("SELECT SUM(v.vaccinationRecordTotal) FROM VaccinationRecord v WHERE v.vaccinationRecordReceiptSource = 'APPOINTMENT' AND v.vaccinationRecordDate BETWEEN :startDate AND :endDate")
    Long sumTotalRevenueByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v FROM VaccinationRecord v WHERE v.vaccinationRecordReceiptSource = 'APPOINTMENT' AND  v.vaccinationRecordDate BETWEEN :startDate AND :endDate")
    List<VaccinationRecord> getAllVaccinationRecordByDateAndSource(@Param("startDate") LocalDate startDate,
                                                                   @Param("endDate") LocalDate endDate);


}
