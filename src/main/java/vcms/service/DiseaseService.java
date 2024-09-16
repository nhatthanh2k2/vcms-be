package vcms.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vcms.dto.request.DiseaseRequest;
import vcms.dto.response.DiseaseResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.DiseaseMapper;
import vcms.model.Disease;
import vcms.model.Vaccine;
import vcms.repository.DiseaseRepository;
import vcms.repository.VaccineRepository;
import vcms.utils.DateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.LongStream;

@Service
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    private final DiseaseMapper diseaseMapper;

    private final DateService dateService;

    private final VaccineRepository vaccineRepository;

    public DiseaseService(DiseaseRepository diseaseRepository,
                          DateService dateService, DiseaseMapper diseaseMapper,
                          VaccineRepository vaccineRepository) {
        this.diseaseRepository = diseaseRepository;
        this.dateService = dateService;
        this.diseaseMapper = diseaseMapper;
        this.vaccineRepository = vaccineRepository;
    }

    public List<DiseaseResponse> getDiseases() {
        return diseaseRepository.findAll().stream()
                .map(diseaseMapper::toDiseaseResponse).toList();
    }

    public DiseaseResponse getDisease(Long diseaseId) {
        return diseaseMapper.toDiseaseResponse(
                diseaseRepository.findById(diseaseId).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DiseaseResponse createDisease(DiseaseRequest request) {
            Disease disease = diseaseMapper.toDisease(request);
            LocalDateTime createDateTime =
                    dateService.getDateTimeNow();
            disease.setDiseaseCreateAt(createDateTime);
            disease.setDiseaseUpdateAt(createDateTime);
        return diseaseMapper.toDiseaseResponse(diseaseRepository.save(disease));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DiseaseResponse updateDisease(Long diseaseId,
                                         DiseaseRequest request) {

        Disease disease = diseaseRepository.findById(diseaseId)
                    .orElseThrow(
                            () -> new AppException(ErrorCode.NOT_EXISTED));
            diseaseMapper.updateDisease(disease, request);
            LocalDateTime updateDateTime =
                    dateService.getDateTimeNow();
            disease.setDiseaseUpdateAt(updateDateTime);
        return diseaseMapper.toDiseaseResponse(diseaseRepository.save(disease));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDisease(Long diseaseId) {
        diseaseRepository.deleteById(diseaseId);
    }

    public void initalDiseaseData() {
        List<Disease> diseaseList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd-MM-yyyy HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        LocalDateTime createDateTime =
                LocalDateTime.parse(formattedDateTime, formatter);
        diseaseList.add(new Disease(
                "Bạch hầu, ho gà, uốn ván, bại liệt và viêm màng não mủ, viêm phổi",
                createDateTime,
                createDateTime));

        diseaseList.add(new Disease("Tiêu chảy cấp do Rota virus",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Các bệnh do phế cầu",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bệnh Lao",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Viêm gan B",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Viêm màng não do não mô cầu nhóm B",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Viêm màng não do não mô cầu nhóm B,C",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(
                new Disease("Viêm màng não do não mô cầu nhóm A, C, Y, W-135",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bệnh Sởi",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Sởi – Quai bị – Rubella",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Thủy đậu",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bệnh Cúm",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease(
                "Ung thư cổ tử cung, ung thư hầu họng, sùi mào gà... do HPV",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Uốn ván",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Viêm não Nhật Bản",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bệnh Dại",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bạch hầu – Uốn ván – Ho gà",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bạch hầu – Ho gà – Uốn ván – Bại liệt",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bạch hầu – Uốn ván",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Viêm gan A + B",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Viêm gan A",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Thương hàn",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Các bệnh do Hib",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bệnh tả",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bệnh sốt vàng",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Bệnh COVID-19",
                                    createDateTime,
                                    createDateTime));
        try {
            diseaseRepository.saveAll(diseaseList);
            System.out.println("Disease Data Inserted Successfully!");
        }
        catch (Exception exception) {
            System.out.println("Disease Data Inserted Failed!");
        }
    }


    public void updateDiseaseVaccineRelations() {
        List<Long> diseaseIds = LongStream.rangeClosed(1, 26)
                .boxed()
                .toList();

        Map<Long, List<Long>> diseaseVaccineMap = new HashMap<>();

        diseaseVaccineMap.put(1L, Arrays.asList(1014L, 1015L, 1016L, 1044L));
        diseaseVaccineMap.put(2L, Arrays.asList(1019L, 1020L, 1021L));
        diseaseVaccineMap.put(3L, Arrays.asList(1005L, 1006L, 1013L));
        diseaseVaccineMap.put(4L, List.of(1022L));
        diseaseVaccineMap.put(5L, Arrays.asList(1010L, 1017L, 1046L, 1047L));
        diseaseVaccineMap.put(6L, List.of(1001L));
        diseaseVaccineMap.put(7L, List.of(1023L));
        diseaseVaccineMap.put(8L, List.of(1045L));
        diseaseVaccineMap.put(9L, List.of(1025L));
        diseaseVaccineMap.put(10L, Arrays.asList(1026L, 1018L, 1043L));
        diseaseVaccineMap.put(11L, Arrays.asList(1008L, 1041L, 1042L));
        diseaseVaccineMap.put(12L, Arrays.asList(1004L, 1012L, 1024L, 1040L));
        diseaseVaccineMap.put(13L, Arrays.asList(1000L, 1011L));
        diseaseVaccineMap.put(14L, Arrays.asList(1033L, 1038L));
        diseaseVaccineMap.put(15L, Arrays.asList(1009L, 1029L, 1048L));
        diseaseVaccineMap.put(16L, Arrays.asList(1002L, 1003L));
        diseaseVaccineMap.put(17L, Arrays.asList(1007L, 1028L));
        diseaseVaccineMap.put(18L, List.of(1027L));
        diseaseVaccineMap.put(19L, List.of(1037L));
        diseaseVaccineMap.put(20L, List.of(1032L));
        diseaseVaccineMap.put(21L, Arrays.asList(1030L, 1031L));
        diseaseVaccineMap.put(22L, Arrays.asList(1034L, 1049L));
        diseaseVaccineMap.put(23L, List.of(1036L));
        diseaseVaccineMap.put(24L, List.of(1035L));
        diseaseVaccineMap.put(25L, List.of(1050L));
        diseaseVaccineMap.put(26L, List.of(1039L));

        for (Long diseaseId : diseaseIds) {
            // Lấy Disease từ database
            Disease disease = diseaseRepository.findById(diseaseId)
                    .orElseThrow(() -> new RuntimeException(
                            "Disease not found with id: " + diseaseId));

            // Lấy danh sách các Vaccine tương ứng từ Map
            List<Long> vaccineIds = diseaseVaccineMap.get(diseaseId);

            // Lấy tất cả Vaccine theo danh sách id
            List<Vaccine> vaccines = vaccineRepository.findAllById(vaccineIds);

            // Thiết lập mối quan hệ giữa Vaccine và Disease
            vaccines.forEach(vaccine -> vaccine.setDisease(disease));

            // Lưu tất cả Vaccine trong một lần
            vaccineRepository.saveAll(vaccines);
        }
    }

}
