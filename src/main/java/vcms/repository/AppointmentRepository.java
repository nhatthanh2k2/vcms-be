package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vcms.enums.AppointmentStatus;
import vcms.model.Appointment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByAppointmentInjectionDate(LocalDate date);

    List<Appointment> findAllByAppointmentStatus(AppointmentStatus status);

    @Query("SELECT v.vaccineName, COUNT(a) FROM Appointment a " +
            "JOIN a.batchDetail bd " +
            "JOIN bd.vaccine v " +
            "WHERE a.appointmentInjectionDate BETWEEN :startDate AND :endDate " +
            "AND a.vaccinePackage IS NULL " +  // Chỉ lấy những cuộc hẹn có vắc xin
            "GROUP BY v.vaccineName")
    List<Object[]> countVaccinesInAppointments(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT vp.vaccinePackageName, COUNT(a) FROM Appointment a " +
            "JOIN a.vaccinePackage vp " +  // Lấy những cuộc hẹn có gói tiêm
            "WHERE a.appointmentInjectionDate BETWEEN :startDate AND :endDate " +
            "AND a.batchDetail IS NULL " +  // Chỉ lấy những cuộc hẹn có gói tiêm
            "GROUP BY vp.vaccinePackageName")
    List<Object[]> countPackagesInAppointments(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);


}
