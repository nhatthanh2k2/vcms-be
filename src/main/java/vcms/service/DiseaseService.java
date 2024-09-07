package vcms.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vcms.dto.request.DiseaseRequest;
import vcms.dto.response.DiseaseRespone;
import vcms.mapper.DiseaseMapper;
import vcms.model.Disease;
import vcms.repository.DiseaseRepository;
import vcms.utils.DateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    private final DiseaseMapper diseaseMapper;

    private final DateService dateService;

    public DiseaseService(DiseaseRepository diseaseRepository,
                          DateService dateService, DiseaseMapper diseaseMapper) {
        this.diseaseRepository = diseaseRepository;
        this.dateService = dateService;
        this.diseaseMapper = diseaseMapper;
    }

    public List<DiseaseRespone> getDiseases() {
        return diseaseRepository.findAll().stream()
                .map(diseaseMapper::toDiseaseResponse).toList();
    }

    public DiseaseRespone getDisease(Long id) {
        return diseaseMapper.toDiseaseResponse(
                diseaseRepository.findById(id).orElseThrow(
                        () -> new RuntimeException("Disease Not Found")));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DiseaseRespone createDisease(DiseaseRequest request) {
            Disease disease = diseaseMapper.toDisease(request);
            LocalDateTime createDateTime =
                    dateService.getDateTimeNow();
            disease.setDiseaseCreateAt(createDateTime);
            disease.setDiseaseUpdateAt(createDateTime);
        return diseaseMapper.toDiseaseResponse(diseaseRepository.save(disease));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DiseaseRespone updateDisease(Long id, DiseaseRequest request) {

            Disease disease = diseaseRepository.findById(id)
                    .orElseThrow(
                            () -> new RuntimeException("Disease Not Found"));
            diseaseMapper.updateDisease(disease, request);
            LocalDateTime updateDateTime =
                    dateService.getDateTimeNow();
            disease.setDiseaseUpdateAt(updateDateTime);
        return diseaseMapper.toDiseaseResponse(diseaseRepository.save(disease));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteDisease(Long id) {
        try {
            diseaseRepository.deleteById(id);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public void insertDiseaseDataToDB() {
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

        diseaseList.add(new Disease("Bệnh Tả",
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
}
