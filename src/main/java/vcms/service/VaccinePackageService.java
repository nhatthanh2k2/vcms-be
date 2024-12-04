package vcms.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vcms.dto.request.VaccinePackageCreationRequest;
import vcms.dto.request.VaccinePackageUpdateRequest;
import vcms.dto.response.PackageDetailResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.DiseaseMapper;
import vcms.mapper.VaccineMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.BatchDetail;
import vcms.model.PackageDetail;
import vcms.model.Vaccine;
import vcms.model.VaccinePackage;
import vcms.repository.VaccinePackageRepository;
import vcms.utils.DateService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VaccinePackageService {

    private final VaccinePackageRepository vaccinePackageRepository;

    private final VaccinePackageMapper vaccinePackageMapper;

    private final DateService dateService;

    private final PackageDetailService packageDetailService;

    private final VaccineService vaccineService;

    private final VaccineMapper vaccineMapper;

    private final DiseaseMapper diseaseMapper;

    private final VaccineBatchService vaccineBatchService;

    private final BatchDetailService batchDetailService;

    public VaccinePackageService(
            VaccinePackageRepository vaccinePackageRepository,
            VaccinePackageMapper vaccinePackageMapper,
            DateService dateService, PackageDetailService packageDetailService,
            VaccineService vaccineService,
            VaccineMapper vaccineMapper, DiseaseMapper diseaseMapper, VaccineBatchService vaccineBatchService,
            BatchDetailService batchDetailService) {
        this.vaccinePackageRepository = vaccinePackageRepository;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.dateService = dateService;
        this.packageDetailService = packageDetailService;
        this.vaccineService = vaccineService;
        this.vaccineMapper = vaccineMapper;
        this.diseaseMapper = diseaseMapper;
        this.vaccineBatchService = vaccineBatchService;
        this.batchDetailService = batchDetailService;
    }

    public List<VaccinePackageResponse> getAllVaccinePackage() {
        return vaccinePackageRepository.findAll().stream()
                .map(vaccinePackageMapper::toVaccinePackageResponse).toList();
    }

    public List<VaccinePackageResponse> getDefaultPackage() {
        return vaccinePackageRepository.findAllByIsCustomPackage(0).stream()
                .map(vaccinePackageMapper::toVaccinePackageResponse).toList();
    }

    public VaccinePackage getVaccinePackageById(Long packageId) {
        return vaccinePackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
    }

    public List<VaccinePackage> getAllByVaccinePackageIdList(List<Long> packageIdList) {
        return vaccinePackageRepository.findAllById(packageIdList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVaccinePackage(Long packageId) {
        VaccinePackage vaccinePackage = vaccinePackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        if (!vaccinePackage.getAppointmentList().isEmpty()
                || !vaccinePackage.getOrderDetailList().isEmpty()
                || !vaccinePackage.getVaccinationRecordList().isEmpty()) {
            throw new AppException(ErrorCode.DELETE_FAILED);
        }
        vaccinePackageRepository.deleteById(packageId);
    }


    @Transactional
    public VaccinePackageResponse updateVaccinePackage(VaccinePackageUpdateRequest request) {
        VaccinePackage existingPackage = vaccinePackageRepository.findById(request.getVaccinePackageId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        existingPackage.setVaccinePackageName(request.getVaccinePackageName());
        existingPackage.setVaccinePackageType(request.getVaccinePackageType());
        existingPackage.setVaccinePackagePrice(request.getVaccinePackagePrice());
        existingPackage.setVaccinePackageUpdateAt(dateService.getDateTimeNow());

        List<Long> newVaccineIds = request.getVaccineIdList();
        List<Integer> newDoseCounts = request.getDoseCountList();

        existingPackage.getPackageDetailList().removeIf(
                detail -> !newVaccineIds.contains(detail.getVaccine().getVaccineId())
        );

        for (int i = 0; i < newVaccineIds.size(); i++) {
            Long vaccineId = newVaccineIds.get(i);
            int doseCount = newDoseCounts.get(i);

            PackageDetail detail = existingPackage.getPackageDetailList().stream()
                    .filter(d -> d.getVaccine().getVaccineId().equals(vaccineId))
                    .findFirst()
                    .orElseGet(() -> {
                        PackageDetail newDetail = new PackageDetail();
                        newDetail.setVaccine(vaccineService.getVaccineByVaccineId(vaccineId));
                        newDetail.setVaccinePackage(existingPackage);
                        existingPackage.getPackageDetailList().add(newDetail);
                        return newDetail;
                    });

            detail.setPackageDetailDoseCount(doseCount);
        }

        return vaccinePackageMapper.toVaccinePackageResponse(
                vaccinePackageRepository.save(existingPackage));
    }

    public void saveVaccinePackage(VaccinePackage vaccinePackage) {
        vaccinePackageRepository.save(vaccinePackage);
    }

    public List<PackageDetailResponse> getDetailsOfPackage(Long vaccinePackageId) {
        VaccinePackage vaccinePackage = vaccinePackageRepository.findById(vaccinePackageId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        List<PackageDetail> packageDetailList =
                packageDetailService.getAllPackageDetailByVaccinePackage(vaccinePackage);
        List<PackageDetailResponse> packageDetailResponseList = new ArrayList<>();

        for (PackageDetail detail : packageDetailList) {
            PackageDetailResponse response = new PackageDetailResponse();
            Vaccine vaccine = detail.getVaccine();
            response.setPackageDetailId(detail.getPackageDetailId());
            response.setPackageDetailDoseCount(detail.getPackageDetailDoseCount());
            response.setVaccineResponse(vaccineMapper.toVaccineResponse(detail.getVaccine()));
            response.setDiseaseResponse(diseaseMapper.toDiseaseResponse(vaccine.getDisease()));
            packageDetailResponseList.add(response);
        }
        return packageDetailResponseList;
    }

    public VaccinePackageResponse addVaccinePackage(VaccinePackageCreationRequest request) {
        VaccinePackage vaccinePackage = new VaccinePackage();
        vaccinePackage.setVaccinePackageName(request.getVaccinePackageName());
        vaccinePackage.setVaccinePackageType(request.getVaccinePackageType());
        vaccinePackage.setIsCustomPackage(0);
        vaccinePackageRepository.save(vaccinePackage);

        List<Long> vaccineIds = request.getVaccineIdList();
        List<Integer> doseCounts = request.getDoseCountList();
        int totalPrice = 0;
        List<PackageDetail> packageDetailList = new ArrayList<>();
        List<BatchDetail> batchDetailList = vaccineBatchService.getDetailListOfSampleBatch();
        for (int i = 0; i < vaccineIds.size(); i++) {
            PackageDetail packageDetail = new PackageDetail();
            Vaccine vaccine = vaccineService.getVaccineByVaccineId(vaccineIds.get(i));
            int vaccinePrice = getVaccinePrice(batchDetailList, vaccinePackage, vaccine);
            packageDetail.setVaccine(vaccine);
            packageDetail.setVaccinePackage(vaccinePackage);
            packageDetail.setPackageDetailDoseCount(doseCounts.get(i));
            packageDetailList.add(packageDetail);
            totalPrice += vaccinePrice * doseCounts.get(i);
        }
        vaccinePackage.setVaccinePackageCreateAt(dateService.getDateTimeNow());
        vaccinePackage.setVaccinePackageUpdateAt(dateService.getDateTimeNow());
        vaccinePackage.setVaccinePackagePrice(totalPrice);
        vaccinePackageRepository.save(vaccinePackage);
        packageDetailService.insertAllPackageDetail(packageDetailList);
        return vaccinePackageMapper.toVaccinePackageResponse(vaccinePackage);
    }


    public void insertPackageToDB(VaccinePackageCreationRequest request) {
        VaccinePackage vaccinePackage = new VaccinePackage();
        vaccinePackage.setVaccinePackageName(request.getVaccinePackageName());
        vaccinePackage.setVaccinePackageType(request.getVaccinePackageType());
        vaccinePackage.setIsCustomPackage(0);
        vaccinePackageRepository.save(vaccinePackage);

        List<Long> vaccineIds = request.getVaccineIdList();
        List<Integer> doseCounts = request.getDoseCountList();
        int totalPrice = 0;
        List<PackageDetail> packageDetailList = new ArrayList<>();
        List<BatchDetail> batchDetailList = vaccineBatchService.getDetailListOfSampleBatch();
        for (int i = 0; i < vaccineIds.size(); i++) {
            PackageDetail packageDetail = new PackageDetail();
            Vaccine vaccine = vaccineService.getVaccineByVaccineId(vaccineIds.get(i));
            int vaccinePrice = getVaccinePrice(batchDetailList, vaccinePackage, vaccine);
            packageDetail.setVaccine(vaccine);
            packageDetail.setVaccinePackage(vaccinePackage);
            packageDetail.setPackageDetailDoseCount(doseCounts.get(i));
            packageDetailList.add(packageDetail);
            totalPrice += vaccinePrice * doseCounts.get(i);
        }
        vaccinePackage.setVaccinePackageCreateAt(dateService.getDateTimeNow());
        vaccinePackage.setVaccinePackageUpdateAt(dateService.getDateTimeNow());
        vaccinePackage.setVaccinePackagePrice(totalPrice);
        vaccinePackageRepository.save(vaccinePackage);
        packageDetailService.insertAllPackageDetail(packageDetailList);
    }

    private static int getVaccinePrice(List<BatchDetail> batchDetailList, VaccinePackage vaccinePackage,
                                       Vaccine vaccine) {
        BatchDetail matchingBatchDetail = batchDetailList.stream()
                .filter(batchDetail -> batchDetail.getVaccine().getVaccineId().equals(vaccine.getVaccineId()))
                .findFirst()
                .orElse(null);

        if (matchingBatchDetail == null) {
            return 0;
        }

        return matchingBatchDetail.getBatchDetailVaccinePrice();
    }

    public Long calculateVaccinePackageCost(VaccinePackage vaccinePackage) {
        Long packageCost = 0L;

        // Lặp qua từng chi tiết gói
        for (PackageDetail detail : vaccinePackage.getPackageDetailList()) {
            Vaccine vaccine = detail.getVaccine();
            int doseCount = detail.getPackageDetailDoseCount();

            // Lấy giá nhập từ lô mới nhất
            BatchDetail latestBatchDetail = batchDetailService.getLatestBatchDetail(vaccine);
            if (latestBatchDetail != null) {
                packageCost += (long) latestBatchDetail.getBatchDetailVaccinePrice() * doseCount;
            }
        }

        return packageCost;
    }


    public void insertInitialVaccinePackageData() {
        List<VaccinePackageCreationRequest> creationRequestList = new ArrayList<>();
        VaccinePackageCreationRequest package1 = new VaccinePackageCreationRequest();
        package1.setVaccinePackageName("Gói vắc xin cho người trưởng thành");
        package1.setVaccinePackageType("ADULT");
        package1.setVaccineIdList(
                Arrays.asList(1008L, 1026L, 1007L, 1033L, 1006L, 1045L, 1002L,
                              1029L, 1005L));
        package1.setDoseCountList(Arrays.asList(2, 2, 1, 2, 1, 1, 2, 1, 1));

        VaccinePackageCreationRequest package2 = new VaccinePackageCreationRequest();
        package2.setVaccinePackageName(
                "Gói vắc xin cho phụ nữ chuẩn bị trước mang thai");
        package2.setVaccinePackageType("ADULT");
        package2.setVaccineIdList(
                Arrays.asList(1008L, 1026L, 1007L, 1033L, 1006L, 1000L, 1045L,
                              1002L, 1029L, 1005L));
        package2.setDoseCountList(Arrays.asList(2, 2, 1, 2, 1, 0, 1, 2, 1, 1));

        VaccinePackageCreationRequest package3 = new VaccinePackageCreationRequest();
        package3.setVaccinePackageName(
                "Gói vắc xin cho tuổi vị thành niên và thanh niên (Từ 9 tuổi - 18 tuổi)");
        package3.setVaccinePackageType("ADOLESCENT");
        package3.setVaccineIdList(
                Arrays.asList(1001L, 1007L, 1006L, 1000L, 1012L, 1029L, 1045L,
                              1002L, 1008L, 1026L, 1032L, 1049L));
        package3.setDoseCountList(
                Arrays.asList(3, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1));

        VaccinePackageCreationRequest package4 = new VaccinePackageCreationRequest();
        package4.setVaccinePackageName("Gói vắc xin cho trẻ tiền học đường (Từ 4 tuổi - 6 tuổi)");
        package4.setVaccinePackageType("CHILD");
        package4.setVaccineIdList(
                Arrays.asList(1027L, 1006L, 1000L, 1012L, 1029L, 1045L, 1002L,
                              1008L, 1026L, 1032L, 1049L, 1035L));
        package4.setDoseCountList(Arrays.asList(1, 1, 1, 1, 2, 1, 2, 2, 1, 2,
                                                1, 2));

        VaccinePackageCreationRequest package5 = new VaccinePackageCreationRequest();
        package5.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi (Gói 6 tháng)");
        package5.setVaccinePackageType("CHILD");
        package5.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L));
        package5.setDoseCountList(Arrays.asList(2, 3, 3, 2, 1));

        VaccinePackageCreationRequest package6 = new VaccinePackageCreationRequest();
        package6.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi (Gói 9 tháng)");
        package6.setVaccinePackageType("CHILD");
        package6.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L, 1025L, 1029L,
                              1045L, 1008L));
        package6.setDoseCountList(Arrays.asList(2, 3, 3, 2, 2, 1, 1, 1, 1));

        VaccinePackageCreationRequest package7 = new VaccinePackageCreationRequest();
        package7.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi (Gói 12 tháng)");
        package7.setVaccinePackageType("CHILD");
        package7.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L, 1025L, 1029L,
                              1045L, 1008L, 1026L, 1032L));
        package7.setDoseCountList(
                Arrays.asList(2, 3, 4, 3, 2, 1, 1, 2, 2, 1, 1));

        VaccinePackageCreationRequest package8 = new VaccinePackageCreationRequest();
        package8.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi (Gói 24 tháng)");
        package8.setVaccinePackageType("CHILD");
        package8.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L, 1025L, 1029L,
                              1045L, 1008L, 1026L, 1032L, 1049L, 1035L));
        package8.setDoseCountList(
                Arrays.asList(2, 4, 4, 3, 3, 1, 2, 2, 2, 1, 2, 1, 2));

        creationRequestList.add(package1);
        creationRequestList.add(package2);
        creationRequestList.add(package3);
        creationRequestList.add(package4);
        creationRequestList.add(package5);
        creationRequestList.add(package6);
        creationRequestList.add(package7);
        creationRequestList.add(package8);
        try {
            for (VaccinePackageCreationRequest request : creationRequestList) {
                insertPackageToDB(request);
            }
            System.out.println("Vaccine Package Data Inserted  Successfully");
        }
        catch (Exception exception) {
            System.out.println("Vaccine Package Data Inserted  Failed");
        }
    }

}
