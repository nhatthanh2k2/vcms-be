package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Disease;
import vcms.model.Vaccine;

import java.util.List;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {

    Vaccine findByVaccineCode(String vaccineCode);

    List<Vaccine> findAllByDisease(Disease disease);
}
