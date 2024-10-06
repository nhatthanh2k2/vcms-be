package vcms.service;

import org.springframework.stereotype.Service;
import vcms.model.PackageDetail;
import vcms.model.VaccinePackage;
import vcms.repository.PackageDetailRepository;

import java.util.List;

@Service
public class PackageDetailService {
    private final PackageDetailRepository packageDetailRepository;

    public PackageDetailService(PackageDetailRepository packageDetailRepository) {
        this.packageDetailRepository = packageDetailRepository;
    }

    public List<PackageDetail> getAllPackageDetailByVaccinePackage(VaccinePackage vaccinePackage) {
        return packageDetailRepository.findAllByVaccinePackage(vaccinePackage);
    }

    public void insertAllPackageDetail(List<PackageDetail> packageDetailList) {
        packageDetailRepository.saveAll(packageDetailList);
    }
}
