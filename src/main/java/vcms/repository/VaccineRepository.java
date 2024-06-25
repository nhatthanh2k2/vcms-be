package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Vaccine;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    boolean existsByVaccineName(String vaccineName);
}
