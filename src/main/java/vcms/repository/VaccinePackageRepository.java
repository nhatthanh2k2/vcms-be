package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.VaccinePackage;

public interface VaccinePackageRepository extends JpaRepository<VaccinePackage, Long> {
}
