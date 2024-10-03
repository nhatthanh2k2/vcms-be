package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.PackageDetail;
import vcms.model.VaccinePackage;

import java.util.List;

public interface PackageDetailRepository extends JpaRepository<PackageDetail, Long> {
    List<PackageDetail> findAllByVaccinePackage(
            VaccinePackage vaccinePackage);
}
