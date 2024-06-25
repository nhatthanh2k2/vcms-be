package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Disease;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    boolean existsByDiseaseName(String disease_name);
}
