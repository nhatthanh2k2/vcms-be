package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
