package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vcms.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
