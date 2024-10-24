package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.VaccinePackage;

import java.util.List;

public interface VaccinePackageRepository extends JpaRepository<VaccinePackage, Long> {
    List<VaccinePackage> findAllByIsCustomPackage(int isCustom);
}
