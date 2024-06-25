package vcms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcms.dto.request.DiseaseRequest;
import vcms.dto.response.ApiResponse;
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

    public ApiResponse<List<Disease>> getDiseases() {
        ApiResponse<List<Disease>> apiResponse = new ApiResponse<>();
        try {
            List<Disease> diseases = diseaseRepository.findAll();
            apiResponse.setResult(diseases);
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(new ArrayList<>());
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<DiseaseRespone> getDisease(Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Disease disease = diseaseRepository.findById(id)
                    .orElseThrow(
                            () -> new RuntimeException("Disease Not Found"));
            apiResponse.setResult(diseaseMapper.toDiseaseResponse(disease));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }

        return apiResponse;
    }

    public ApiResponse<DiseaseRespone> createDisease(DiseaseRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Disease disease = diseaseMapper.toDisease(request);
            LocalDateTime createDateTime =
                    dateService.getDateTimeNow();
            disease.setDiseaseCreateAt(createDateTime);
            disease.setDiseaseUpdateAt(createDateTime);
            apiResponse.setResult(diseaseMapper.toDiseaseResponse(
                    diseaseRepository.save(disease)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setSuccess(false);
            apiResponse.setResult(null);
        }
        return apiResponse;
    }

    public ApiResponse<DiseaseRespone> updateDisease(Long id,
                                                     DiseaseRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Disease disease = diseaseRepository.findById(id)
                    .orElseThrow(
                            () -> new RuntimeException("Disease Not Found"));
            diseaseMapper.updateDisease(disease, request);
            LocalDateTime updateDateTime =
                    dateService.getDateTimeNow();
            disease.setDiseaseUpdateAt(updateDateTime);
            apiResponse.setResult(diseaseMapper.toDiseaseResponse(
                    diseaseRepository.save(disease)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }

        return apiResponse;
    }

    public ApiResponse<String> deleteDisease(Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            diseaseRepository.deleteById(id);
            apiResponse.setResult("Disease deleted successfully");
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult("Disease deleted failed");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public void insertDiseaseDataToDatabase() {
        List<Disease> diseaseList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd-MM-yyyy HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        LocalDateTime createDateTime =
                LocalDateTime.parse(formattedDateTime, formatter);
        diseaseList.add(new Disease("Vắc xin viêm gan B",
                                    "Viêm gan B là một bệnh lây nhiễm gan do virus viêm gan B gây ra, có thể gây viêm gan mãn tính và ung thư gan.",
                                    "Tiêm phòng vắc-xin, tránh tiếp xúc với " +
                                            "máu hoặc các chất lỏng cơ thể của người bệnh, sử dụng bao cao su khi quan hệ tình dục.",
                                    createDateTime,
                                    createDateTime));

        diseaseList.add(new Disease("Vắc xin viêm gan A",
                                    "Viêm gan A là một bệnh lây nhiễm gan do virus viêm gan A gây ra, thường do tiếp xúc với thức ăn hoặc nước bị nhiễm.",
                                    "Tiêm phòng vắc-xin, sử dụng nước sạch và" +
                                            " vệ sinh tay sau khi đi vệ sinh, trước khi chuẩn bị thực phẩm.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin Rota virus",
                                    "Rota virus là nguyên nhân phổ biến của tiêu chảy cấp tính ở trẻ em, thường lan truyền qua tiếp xúc với chất lỏng cơ thể nhiễm virus.",
                                    "Vệ sinh tay thường xuyên, tiêm phòng nếu" +
                                            " có",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin viêm màng não",
                                    "Viêm màng não là bệnh viêm nhiễm của " +
                                            "màng não và tuỷ sống, do nhiều loại virus gây ra  như virus Herpes simplex, virus mumps (quai bị), virus rubeola (sởi)",
                                    "Tiêm phòng khi có vaccine có sẵn (ví dụ " +
                                            "vaccine phòng sởi, quai bị), tránh tiếp xúc với người bệnh, sử dụng bảo hộ khi cần thiết.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin phế cầu khuẩn",
                                    "Phế cầu khuẩn là một loại bệnh lây nhiễm do vi khuẩn Streptococcus pneumoniae gây ra, thường ảnh hưởng đến hệ hô hấp và có thể lan sang các bộ phận khác của cơ thể.",
                                    "Tiêm phòng vắc-xin phòng phế cầu, vệ " +
                                            "sinh tay thường xuyên, tránh tiếp xúc với người bệnh.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin phòng lao",
                                    "Lao là một bệnh lây nhiễm do vi khuẩn Mycobacterium tuberculosis gây ra, thường ảnh hưởng đến phổi.",
                                    "Sử dụng phương pháp tiêm phòng, điều trị" +
                                            " và cách ly người bệnh.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin phòng HPV",
                                    "Virus HPV có thể gây ra các vấn đề sức khỏe " +
                                            "nghiêm trọng như ung thư cổ tử cung, ung thư âm đạo, ung thư hậu môn, và các bệnh lây nhiễm khác.",
                                    "Tiêm phòng vaccine HPV, thường xuyên " +
                                            "kiểm tra sức khỏe và xét nghiệm sớm.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin uốn ván",
                                    "Uốn ván là một bệnh lây nhiễm do virus Poliovirus gây ra, ảnh hưởng đến hệ thần kinh.",
                                    "Tiêm phòng vaccine uốn ván, kiểm tra và " +
                                            "điều trị sớm khi phát hiện.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin thương hàn",
                                    "Thương hàn là một bệnh virus lây nhiễm do virus thương hàn gây ra, thường lây truyền qua côn trùng như muỗi.",
                                    "Tiêm phòng vaccine thương hàn, sử dụng " +
                                            "các biện pháp phòng ngừa muỗi như sử dụng cửa lưới, áo khoác dài, và sử dụng thuốc phòng muỗi.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin phòng tả",
                                    "Tả là một bệnh lây nhiễm đường ruột do vi khuẩn Vibrio cholerae gây ra, phổ biến trong môi trường nước bị ô nhiễm.",
                                    "Vệ sinh thực phẩm, sử dụng nước sạch và " +
                                            "tiêm phòng nếu có.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin phòng dại",
                                    "Dại là một bệnh nhiễm trùng nghiêm trọng do virus dại gây ra, ảnh hưởng đến hệ thần kinh.",
                                    "Tiêm phòng vaccine dại đầy đủ, tránh " +
                                            "tiếp xúc với động vật hoang dã và vật nuôi không được tiêm phòng.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin phòng cúm",
                                    "Cúm là một bệnh lây nhiễm của đường hô hấp do virus cúm gây ra, có thể gây ra dịch bệnh mùa và dịch bệnh lây lan rộng.",
                                    "Tiêm phòng vaccine cúm mỗi năm, vệ sinh " +
                                            "tay thường xuyên, tránh tiếp xúc với người bệnh.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin sốt vàng",
                                    "Sốt vàng là một bệnh virus lây nhiễm do virus sốt vàng gây ra, lây truyền qua côn trùng như muỗi.",
                                    "Tiêm phòng vaccine sốt vàng, sử dụng các" +
                                            " biện pháp phòng ngừa muỗi như sử dụng cửa lưới, áo khoác dài, và sử dụng thuốc phòng muỗi.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin thủy đậu",
                                    "Thủy đậu (mumps) là một bệnh lây nhiễm do virus Paramyxovirus gây ra. Đây là một trong những bệnh lây nhiễm thông thường ở trẻ em, nhưng cũng có thể ảnh hưởng đến người lớn.",
                                    "Tiêm phòng vaccine MMR (phòng thủy đậu, " +
                                            "sởi và quai bị) là biện pháp chính để ngăn ngừa bệnh. Ngoài ra, giữ vệ sinh cá nhân tốt và tránh tiếp xúc với người bệnh là các biện pháp phòng ngừa hiệu quả.",
                                    createDateTime,
                                    createDateTime));
        diseaseList.add(new Disease("Vắc xin Covid-19",
                                    "COVID-19 là một bệnh lây nhiễm hô hấp do coronavirus SARS-CoV-2 gây ra, lan truyền chủ yếu qua tiếp xúc với giọt bắn hơi hoặc tiếp xúc gần với người nhiễm bệnh.",
                                    "Đeo khẩu trang, giữ khoảng cách xã hội, " +
                                            "rửa tay thường xuyên, tiêm phòng vaccine COVID-19 khi có sẵn.",
                                    createDateTime,
                                    createDateTime));
        try {
            System.out.println("Start Adding Data To The Database!");
            diseaseRepository.saveAll(diseaseList);
            System.out.println("Disease Data Inserted Successfully!");
        }
        catch (Exception exception) {
            System.out.println("Disease Data Inserted Failed!");
        }
    }
}
