package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.VaccinePackage;
import vcms.model.VaccinePackageDetail;

import java.util.List;

public interface VaccinePackageDetailRepository extends JpaRepository<VaccinePackageDetail, Long> {
    List<VaccinePackageDetail> findAllByVaccinePackage(
            VaccinePackage vaccinePackage);
}
