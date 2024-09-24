package vcms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcms.dto.request.VaccinePackageCreationRequest;
import vcms.dto.response.VaccinePackageDetailResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.DiseaseMapper;
import vcms.mapper.VaccineMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.BatchDetail;
import vcms.model.Vaccine;
import vcms.model.VaccinePackage;
import vcms.model.VaccinePackageDetail;
import vcms.repository.BatchDetailRepository;
import vcms.repository.VaccinePackageDetailRepository;
import vcms.repository.VaccinePackageRepository;
import vcms.repository.VaccineRepository;
import vcms.utils.DateService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VaccinePackageService {

    private final VaccinePackageRepository vaccinePackageRepository;

    private final VaccinePackageMapper vaccinePackageMapper;

    private final DateService dateService;

    private final VaccinePackageDetailRepository vaccinePackageDetailRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private BatchDetailRepository batchDetailRepository;

    @Autowired
    private VaccineMapper vaccineMapper;

    @Autowired
    private DiseaseMapper diseaseMapper;

    public VaccinePackageService(
            VaccinePackageRepository vaccinePackageRepository,
            VaccinePackageMapper vaccinePackageMapper,
            DateService dateService,
            VaccinePackageDetailRepository vaccinePackageDetailRepository) {
        this.vaccinePackageRepository = vaccinePackageRepository;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.dateService = dateService;
        this.vaccinePackageDetailRepository = vaccinePackageDetailRepository;
    }

    public List<VaccinePackageResponse> getAllVaccinePackage() {
        return vaccinePackageRepository.findAll().stream()
                .map(vaccinePackageMapper::toVaccinePackageResponse).toList();
    }

    public List<VaccinePackageDetailResponse> getDetailsOfPackage(
            Long vaccinePackageId) {
        VaccinePackage vaccinePackage = vaccinePackageRepository.findById(
                        vaccinePackageId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        List<VaccinePackageDetail> vaccinePackageDetailList =
                vaccinePackageDetailRepository.findAllByVaccinePackage(
                        vaccinePackage);
        List<VaccinePackageDetailResponse> vaccinePackageDetailResponseList = new ArrayList<>();

        for (VaccinePackageDetail detail : vaccinePackageDetailList) {
            VaccinePackageDetailResponse response = new VaccinePackageDetailResponse();
            Vaccine vaccine = detail.getVaccine();
            response.setVaccinePkgDetailId(detail.getVaccinePkgDetailId());
            response.setDoseCount(detail.getDoseCount());
            response.setVaccineResponse(
                    vaccineMapper.toVaccineResponse(detail.getVaccine()));
            response.setDiseaseResponse(
                    diseaseMapper.toDiseaseResponse(vaccine.getDisease()));
            vaccinePackageDetailResponseList.add(response);
        }
        return vaccinePackageDetailResponseList;
    }



//    public VaccinePackageResponse createVaccinePackage(
//            VaccinePackageCreationRequest vaccinePackageCreationRequest){
//
//    }


    public void insertPackageToDB(VaccinePackageCreationRequest request) {
        VaccinePackage vaccinePackage = new VaccinePackage();
        vaccinePackage.setVaccinePackageName(request.getVaccinePackageName());
        vaccinePackage.setVaccinePackageType(request.getVaccinePackageType());

        vaccinePackageRepository.save(vaccinePackage);

        List<Long> vaccineIds = request.getVaccineIdList();
        List<Integer> doseCounts = request.getDoseCountList();
        int totalPrice = 0;
        List<VaccinePackageDetail> packageDetailList = new ArrayList<>();

        for (int i = 0; i < vaccineIds.size(); i++) {
            VaccinePackageDetail vaccinePackageDetail = new VaccinePackageDetail();
            Vaccine vaccine = vaccineRepository.findById(vaccineIds.get(i))
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            List<BatchDetail> batchDetailList = batchDetailRepository.findByVaccine(
                    vaccine);
            int price = 0;

            if (batchDetailList.size() == 1) {
                BatchDetail batchDetail = batchDetailList.getFirst();
                price = batchDetail.getBatchDetailVaccinePrice();
            }
            else if (batchDetailList.size() > 1) {

                int adultPrice = Math.max(
                        batchDetailList.get(0).getBatchDetailVaccinePrice(),
                        batchDetailList.get(1).getBatchDetailVaccinePrice());
                int childPrice = Math.min(
                        batchDetailList.get(0).getBatchDetailVaccinePrice(),
                        batchDetailList.get(1).getBatchDetailVaccinePrice());
                ;
                if ("ADULT".equals(vaccinePackage.getVaccinePackageType()) ||
                        "B-PREGNANCY".equals(
                                vaccinePackage.getVaccinePackageType()) ||
                        "9-18Y".equals(
                                vaccinePackage.getVaccinePackageType())) {
                    price = adultPrice;
                }
                else {
                    price = childPrice;
                }
            }

            vaccinePackageDetail.setVaccine(vaccine);
            vaccinePackageDetail.setVaccinePackage(vaccinePackage);
            vaccinePackageDetail.setDoseCount(doseCounts.get(i));
            packageDetailList.add(vaccinePackageDetail);
            totalPrice += price * doseCounts.get(i);
        }
        vaccinePackage.setVaccinePackageCreateAt(dateService.getDateTimeNow());
        vaccinePackage.setVaccinePackageUpdateAt(dateService.getDateTimeNow());
        vaccinePackage.setVaccinePackagePrice(totalPrice);
        vaccinePackageRepository.save(vaccinePackage);
        vaccinePackageDetailRepository.saveAll(packageDetailList);
    }

    public void initalVaccinePackageData() {
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
        package2.setVaccinePackageType("B-PREGNANCY");
        package2.setVaccineIdList(
                Arrays.asList(1008L, 1026L, 1007L, 1033L, 1006L, 1000L, 1045L,
                              1002L, 1029L, 1005L));
        package2.setDoseCountList(Arrays.asList(2, 2, 1, 2, 1, 0, 1, 2, 1, 1));

        VaccinePackageCreationRequest package3 = new VaccinePackageCreationRequest();
        package3.setVaccinePackageName(
                "Gói vắc xin cho tuổi vị thành niên và thanh niên (Từ 9 tuổi - 18 tuổi)");
        package3.setVaccinePackageType("9-18Y");
        package3.setVaccineIdList(
                Arrays.asList(1011L, 1007L, 1006L, 1000L, 1012L, 1029L, 1045L,
                              1002L, 1008L, 1026L, 1032L, 1049L));
        package3.setDoseCountList(
                Arrays.asList(3, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1));

        VaccinePackageCreationRequest package4 = new VaccinePackageCreationRequest();
        package4.setVaccinePackageName(
                "Gói vắc xin cho tuổi vị thành niên và thanh niên (Từ 9 tuổi - 18 tuổi)");
        package4.setVaccinePackageType("9-18Y");
        package4.setVaccineIdList(
                Arrays.asList(1001L, 1007L, 1006L, 1000L, 1012L, 1029L, 1045L,
                              1002L, 1008L, 1026L, 1032L, 1049L));
        package4.setDoseCountList(
                Arrays.asList(3, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1));

        VaccinePackageCreationRequest package5 = new VaccinePackageCreationRequest();
        package5.setVaccinePackageName("Gói vắc xin cho trẻ tiền học đường");
        package5.setVaccinePackageType("PRE-SCHOOL");
        package5.setVaccineIdList(
                Arrays.asList(1027L, 1006L, 1000L, 1012L, 1029L, 1045L, 1002L,
                              1008L, 1026L, 1032L, 1049L, 1035L));
        package5.setDoseCountList(Arrays.asList(1, 1, 1, 1, 2, 1, 2, 2, 1, 2,
                                                1, 2));

        VaccinePackageCreationRequest package6 = new VaccinePackageCreationRequest();
        package6.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi");
        package6.setVaccinePackageType("6-MONTH");
        package6.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L));
        package6.setDoseCountList(Arrays.asList(2, 3, 3, 2, 1));

        VaccinePackageCreationRequest package7 = new VaccinePackageCreationRequest();
        package7.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi");
        package7.setVaccinePackageType("9-MONTH");
        package7.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L, 1025L, 1029L,
                              1045L, 1008L));
        package7.setDoseCountList(Arrays.asList(2, 3, 3, 2, 2, 1, 1, 1, 1));

        VaccinePackageCreationRequest package8 = new VaccinePackageCreationRequest();
        package8.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi");
        package8.setVaccinePackageType("12-MONTH");
        package8.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L, 1025L, 1029L,
                              1045L, 1008L, 1026L, 1032L));
        package8.setDoseCountList(
                Arrays.asList(2, 3, 4, 3, 2, 1, 1, 2, 2, 1, 1));

        VaccinePackageCreationRequest package9 = new VaccinePackageCreationRequest();
        package9.setVaccinePackageName("Gói vắc xin cho trẻ em 0-2 tuổi");
        package9.setVaccinePackageType("24-MONTH");
        package9.setVaccineIdList(
                Arrays.asList(1019L, 1015L, 1013L, 1002L, 1005L, 1025L, 1029L,
                              1045L, 1008L, 1026L, 1032L, 1049L, 1035L));
        package9.setDoseCountList(
                Arrays.asList(2, 4, 4, 3, 3, 1, 2, 2, 2, 1, 2, 1, 2));

        creationRequestList.add(package1);
        creationRequestList.add(package2);
        creationRequestList.add(package3);
        creationRequestList.add(package4);
        creationRequestList.add(package5);
        creationRequestList.add(package6);
        creationRequestList.add(package7);
        creationRequestList.add(package8);
        creationRequestList.add(package9);
        try {
            for (VaccinePackageCreationRequest request : creationRequestList) {
                insertPackageToDB(request);
            }
            System.out.println("Insert Vaccine Package Data Successfully");
        }
        catch (Exception exception) {
            System.out.println("Insert Vaccine Package Data Failed");
        }
    }

}
