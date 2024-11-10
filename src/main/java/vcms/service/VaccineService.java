package vcms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vcms.dto.request.VaccineCreationRequest;
import vcms.dto.request.VaccineCreationRequestByAdmin;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.VaccineResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.DiseaseMapper;
import vcms.mapper.VaccineMapper;
import vcms.model.Disease;
import vcms.model.Vaccine;
import vcms.repository.VaccineRepository;
import vcms.utils.DateService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.LongStream;

@Service
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    private final VaccineMapper vaccineMapper;

    private final DateService dateService;

    @Value("${upload.vaccineFolder}")
    private String UPLOAD_VACCINE_FOLDER;

    private final DiseaseService diseaseService;

    private final DiseaseMapper diseaseMapper;

    public VaccineService(VaccineRepository vaccineRepository,
                          VaccineMapper vaccineMapper, DateService dateService,
                          DiseaseService diseaseService, DiseaseMapper diseaseMapper) {
        this.vaccineRepository = vaccineRepository;
        this.vaccineMapper = vaccineMapper;
        this.dateService = dateService;
        this.diseaseService = diseaseService;
        this.diseaseMapper = diseaseMapper;
    }

    public List<VaccineResponse> getAllVaccines() {
        List<Vaccine> vaccineList = vaccineRepository.findAll();
        List<VaccineResponse> vaccineResponseList = new ArrayList<>();

        for (Vaccine vaccine : vaccineList) {
            VaccineResponse response = vaccineMapper.toVaccineResponse(vaccine);
            response.setDiseaseResponse(diseaseMapper.toDiseaseResponse(vaccine.getDisease()));
            vaccineResponseList.add(response);
        }
        return vaccineResponseList;
    }

    public List<VaccineResponse> getVaccineOfDisease(Long diseaseId) {
        Disease disease = diseaseService.getDiseaseById(diseaseId);
        List<Vaccine> vaccineList = getAllVaccinesByDisease(disease);
        return vaccineList.stream().map(vaccineMapper::toVaccineResponse).toList();
    }

    public VaccineResponse getVaccineById(Long vaccineId) {
        return vaccineMapper.toVaccineResponse(
                vaccineRepository.findById(vaccineId).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_EXISTED)));
    }

    public Vaccine getVaccineByVaccineId(Long vaccineId) {
        return vaccineRepository.findById(vaccineId).orElseThrow(
                () -> new AppException(ErrorCode.NOT_EXISTED));
    }

    public List<Vaccine> getAllVaccinesByDisease(Disease disease) {
        return vaccineRepository.findAllByDisease(disease);
    }

    public Vaccine getVaccineByVaccineCode(String vaccineCode) {
        return vaccineRepository.findByVaccineCode(vaccineCode);
    }

    public void insertAllVaccines(List<Vaccine> vaccineList) {
        vaccineRepository.saveAll(vaccineList);
    }

    public List<Vaccine> getAllVaccinesByListId(List<Long> vaccineIdList) {
        return vaccineRepository.findAllById(vaccineIdList);
    }

    public VaccineResponse createVaccine(VaccineCreationRequestByAdmin request) {
        Vaccine vaccine = vaccineMapper.toVaccineFromRequestByAdmin(request);
        String contentType = request.getVaccineImageFile().getContentType();
        String fileExtension = "";
        if ("image/jpeg".equals(contentType)) {
            fileExtension = ".jpg";
        }
        else if ("image/png".equals(contentType)) {
            fileExtension = ".png";
        }
        else {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }
        String originalFilename = request.getVaccineImageFile().getOriginalFilename();
        Path avatarFolderPath = Paths.get(UPLOAD_VACCINE_FOLDER).toAbsolutePath().normalize();
        String newFilePath = avatarFolderPath.resolve(originalFilename).toString();

        try {
            request.getVaccineImageFile().transferTo(new File(newFilePath));
            vaccine.setVaccineImage(originalFilename);
        }
        catch (Exception ex) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }
        Disease disease = diseaseService.getDiseaseById(request.getDiseaseId());
        vaccine.setDisease(disease);
        vaccineRepository.save(vaccine);
        String vaccineCode = "VAC" + vaccine.getVaccineId().toString();
        vaccine.setVaccineCode(vaccineCode);
        LocalDateTime createDateTime = dateService.getDateTimeNow();
        vaccine.setVaccineCreateAt(createDateTime);
        vaccine.setVaccineUpdateAt(createDateTime);
        return vaccineMapper.toVaccineResponse(vaccineRepository.save(vaccine));
    }

    public VaccineResponse updateVaccine(Long vaccineId,
                                         VaccineUpdateRequest request) {
        Vaccine vaccine = vaccineRepository.findById(vaccineId).orElseThrow(
                () -> new AppException(ErrorCode.NOT_EXISTED));
        Long currentDiseaseId = vaccine.getDisease() != null ? vaccine.getDisease().getDiseaseId() : null;

        // Kiểm tra xem diseaseId trong request có khác với currentDiseaseId
        if (request.getDiseaseId() != null && !request.getDiseaseId().equals(currentDiseaseId)) {
            Disease newDisease = diseaseService.getDiseaseById(request.getDiseaseId());
            vaccine.setDisease(newDisease);
        }
        vaccineMapper.updateVaccine(vaccine, request);
        LocalDateTime updateDateTime = dateService.getDateTimeNow();
        vaccine.setVaccineUpdateAt(updateDateTime);
        return vaccineMapper.toVaccineResponse(vaccineRepository.save(vaccine));
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVaccine(Long vaccineId) {
        Vaccine vaccine = vaccineRepository.findById(vaccineId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        if (!vaccine.getBatchDetailList().isEmpty() || !vaccine.getVaccinationRecordList().isEmpty()) {
            throw new AppException(ErrorCode.DELETE_FAILED);
        }
        vaccineRepository.deleteById(vaccineId);
    }

    public void insertInitialVaccineData() {
        LocalDateTime createDateTime = dateService.getDateTimeNow();
        List<VaccineCreationRequest> vaccineCreationRequestList = new ArrayList<>();

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Pneumovax 23 (Mỹ)",
                                           "Pneumovax-23.jpg",
                                           new HashSet<>(Arrays.asList("4-6 tuổi", "9-18 tuổi")),
                                           "Pneumovax 23, hay còn được biết đến với tên gọi vắc xin Polysaccharide phế cầu 23-valent, là vắc xin được chỉ định để ngăn ngừa các bệnh nhiễm trùng do vi khuẩn phế cầu (Streptococcus pneumoniae) gây ra như viêm phổi, viêm màng não, nhiễm khuẩn huyết (nhiễm trùng máu)…",
                                           "Merck Sharp & Dohme (MSD)",
                                           "Pneumovax 23 được tiêm dưới dạng dung dịch trong lọ đơn liều 0,5ml, qua đường tiêm bắp hoặc tiêm dưới da, thường là vào bắp tay (cơ delta) ở người lớn.;Không được tiêm vào mạch máu và phải thận trọng để đảm bảo kim không đi vào mạch máu.;Không được tiêm vắc xin trong da vì có liên quan đến tăng các phản ứng tại chỗ.",
                                           "Những người có tiền sử dị ứng nặng, quá mẫn với bất kỳ thành phần, tá dược nào của vắc xin.;Người đang bị sốt cao hoặc có tình trạng nhiễm trùng cấp tính nên hoãn tiêm phòng cho tới khi hồi phục.",
                                           "Phản ứng tại vị trí tiêm: Đau, đỏ, sưng, nóng, chai cứng;Phản ứng toàn thân: Sốt nhẹ, đau cơ, đau đầu, mệt mỏi",
                                           1, 1,
                                           "Pneumovax 23 cần được bảo quản ở nhiệt độ từ 2°C đến 8°C (36°F đến 46°F), tránh đông lạnh. Phải đảm bảo rằng vắc xin được bảo quản đúng quy định trước khi sử dụng để đảm bảo hiệu quả tối đa.",
                                           "Lịch tiêm cơ bản:;Trẻ em từ 2 tuổi trở lên và người lớn: Tiêm 01 liều cơ bản;Không khuyến cáo tiêm cho trẻ em dưới 2 tuổi vì độ an toàn và hiệu quả của vắc xin chưa được xác định và đáp ứng kháng thể có thể kém.;Lịch tiêm chủng lại: Người có nguy cơ cao mắc bệnh phế cầu xâm lấn (≥2 tuổi): tiêm chủng lại 5 năm sau liều cơ bản hoặc theo chỉ định của bác sĩ điều trị.",
                                           "Vắc xin Pneumovax 23 được khuyến cáo sử dụng cho trẻ em từ 2 tuổi trở lên, thanh thiếu niên và người lớn"));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Gardasil 9 (Mỹ)",
                                           "vac-xin-gardasil-9.jpg",
                                           new HashSet<>(List.of("9-18 tuổi")),
                                           "Vắc xin thế hệ mới Gardasil 9 được xem là vắc xin bình đẳng giới vì mở rộng cả đối tượng và phạm vi phòng bệnh rộng hơn ở nam và nữ giới, bảo vệ khỏi 9 tuýp virus HPV phổ biến 6, 11, 16, 18, 31, 33, 45, 52 và 58 gây bệnh ung thư cổ tử cung, ung thư âm hộ, ung thư âm đạo, ung thư hậu môn, ung thư hầu họng, mụn cóc sinh dục, các tổn thương tiền ung thư hoặc loạn sản…, với hiệu quả bảo vệ lên đến trên 90%.",
                                           "Merck Sharp & Dohme (MSD – Mỹ)",
                                           "Vắc xin Gardasil 9 được chỉ định tiêm bắp. Vị trí phù hợp là vùng cơ delta của phần trên cánh tay hoặc ở vùng trước phía trên đùi.;Không được tiêm Gardasil 9 vào mạch máu, tiêm dưới da hoặc tiêm trong da.;Không được trộn lẫn vắc xin trong cùng một ống tiêm với bất kỳ loại vắc xin và dung dịch nào khác.;",
                                           "Người quá mẫn với các hoạt chất hoặc với bất kỳ tá dược nào của vắc xin được liệt kê trong phần “Thành phần”.;Những người bị quá mẫn sau khi tiêm Gardasil 9 hoặc Gardasil trước đây không nên tiêm Gardasil 9.",
                                           "Phản ứng tại chỗ tiêm: Nổi ban đỏ, chai cứng, sưng đau, ngứa, tăng nhạy cảm tại chỗ tiêm.;Các phản ứng toàn thân khác: Sốt nhẹ, đau đầu, buồn ngủ, giảm cảm giác thèm ăn, buồn nôn, nôn mửa, tiêu chảy.",
                                           2, 3,
                                           "Vắc xin Gardasil 9 được bảo quản trong tủ lạnh (2°C – 8°C). Không để đông lạnh.",
                                           "Người từ tròn 9 tuổi đến dưới 15 tuổi tại thời điểm tiêm lần đầu tiên:; Mũi 1: lần tiêm đầu tiên trong độ tuổi;Mũi 2: cách mũi 1 từ 6-12 tháng.; Người từ tròn 15 tuổi đến 45 tuổi tại thời điểm tiêm lần đầu tiên:; Mũi 1: lần tiêm đầu tiên trong độ tuổi;Mũi 2: cách mũi 1 ít nhất 2 tháng;Mũi 3: cách mũi 2 ít nhất 4 tháng.;",
                                           "Vắc xin Gardasil 9 được chỉ định tiêm chủng cho cả nam giới và nữ giới, từ 9 đến 45 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Bexsero (Ý)",
                                           "vac-xin-bexsero-1.jpg",
                                           new HashSet<>(List.of("9-18 tuổi")),
                                           "Vắc xin Bexsero được chỉ định để chủng ngừa cho trẻ và người lớn từ 2 tháng tuổi đến 50 tuổi (chưa đến sinh nhật 51 tuổi) chống lại bệnh não mô cầu xâm lấn do Neisseria meningitidis nhóm B gây ra với hiệu quả lên đến 95%.",
                                           "Glaxosmithkline – GSK",
                                           "Vắc xin Bexsero được dùng dưới dạng tiêm bắp sâu, nên ưu tiên tiêm ở mặt trước bên cơ đùi của nhũ nhi hoặc vùng cơ delta cánh tay trên ở những đối tượng lớn hơn.;Nếu phải tiêm đồng thời nhiều loại vắc xin khác thì phải tiêm ở nhiều vị trí riêng biệt.",
                                           "Quá mẫn với các hoạt chất hoặc bất kỳ tá dược nào được liệt kê trong bảng thành phần của vắc xin.",
                                           "Ở trẻ nhũ nhi và trẻ nhỏ (từ 10 tuổi trở xuống), các phản ứng bất lợi thường gặp tại vị trí tiêm và toàn thân bao gồm đau, sưng, đỏ, chai cứng tại vị trí tiêm, sốt, quấy khóc, ăn ít hơn bình thường.;Ở thanh thiếu niên (11 tuổi trở lên) và người trưởng thành, các phản ứng bất lợi thường gặp tại chỗ và toàn thân là đau, sưng, đỏ, chai cứng tại vị trí tiêm, khó chịu, đau đầu, Không gia tăng đối với tần suất gặp và mức độ trầm trọng của các phản ứng không mong muốn ở các mũi tiêm sau trong các liệu trình tiêm chủng.",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C.",
                                           "Trẻ từ 2 tháng tuổi đến < 6 tháng tuổi có lịch tiêm 2 mũi cơ bản:;Mũi 1: lần tiêm đầu tiên;Mũi 2: cách mũi 1 ít nhất 2 tháng; Trẻ từ tròn 6 tháng đến dưới 12 tháng tuổi có lịch tiêm 2 mũi cơ bản:;Mũi 1: lần tiêm đầu tiên;Mũi 2: cách mũi 1 ít nhất 2 tháng; Trẻ từ tròn 1 tuổi đến dưới 2 tuổi có lịch tiêm 2 mũi cơ bản:;Mũi 1: lần tiêm đầu tiên;Mũi 2: cách mũi 1 ít nhất 2 tháng;Trẻ từ tròn 2 tuổi đến 50 tuổi có lịch tiêm 2 mũi cơ bản:;Mũi 1: lần tiêm đầu tiên;Mũi 2: cách mũi 1 ít nhất 1 tháng;",
                                           "Trẻ em và người lớn từ 2 tháng tuổi đến 50 tuổi (chưa đến sinh nhật 51 tuổi)."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Abhayrab (Ấn Độ)",
                                           "vac-xin-abhayrab.jpg",
                                           new HashSet<>(List.of("Ngoài các nhóm trên")),
                                           "Vắc xin Abhayrab có tác dụng tạo miễn dịch chủ động phòng bệnh dại cho cả người lớn và trẻ em, sau khi tiếp xúc hoặc bị con vật nghi bị dại cắn.",
                                           "Human Biological Institute (Ấn Độ) ",
                                           "Tiêm bắp (IM): người lớn tiêm ở vùng cơ Delta cánh tay, trẻ em tiêm ở mặt trước bên đùi. Không tiêm vào vùng mông.;Trong một số trường hợp có thể áp dụng tiêm trong da (ID), tiêm ở cẳng tay hoặc cánh tay.",
                                           "Không tiêm bắp ở người có rối loạn chảy máu như hemophilia hoặc giảm tiểu cầu.;Hoãn tiêm khi khách hàng có sốt nhiễm trùng nặng, bệnh cấp tính, đợt tiến triển của bệnh mạn tính.;Mẫn cảm với bất kỳ thành phần nào của vắc xin.;Bệnh dại là bệnh rất nguy hiểm, do vậy không có chống chỉ định nào trong trường hợp điều trị sau phơi nhiễm.",
                                           "Tại chỗ tiêm: đau, quầng đỏ, sưng, ngứa và nốt cứng;Toàn thân: sốt, đau đầu, chóng mặt, mệt mỏi.",
                                           3, 3,
                                           "Bảo quản ở nhiệt độ lạnh (từ 2 – 8 độ C). Không được đóng băng.",
                                           "Lịch tiêm vắc xin dại dự phòng trước phơi nhiễm gồm 3 mũi: Vào các ngày 0-7-21 hoặc (28).; Lịch tiêm khi xác định có phơi nhiễm:; Tiêm 4 mũi (*): vào các ngày N0 – N3 – N7 – N28;Tiêm 5 mũi (**) vào các ngày N0 – N3 – N7- N14 – N28; (*) Con vật sống sau 10 ngày theo dõi;(**) Con vật chết, bệnh, không theo dõi được;Người đã tiêm dự phòng trước phơi nhiễm hoặc sau phơi nhiễm ít nhất 3 mũi vắc xin Dại:Tiêm 2 mũi vào các ngày 0-3.\n",
                                           "Vắc xin Abhayrab phòng bệnh dại được chỉ định tiêm cho trẻ em và người lớn ở mọi lứa tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Verorab (Pháp)",
                                           "vac-xin-verorab.jpg",
                                           new HashSet<>(List.of("Ngoài các nhóm trên")),
                                           "Vắc xin Verorab có tác dụng tạo miễn dịch chủ động phòng bệnh dại cho cả người lớn và trẻ em, sau khi tiếp xúc hoặc bị con vật nghi bị dại cắn.",
                                           "Sanofi Pasteur (Pháp)",
                                           "Tiêm bắp: với liều 0.5ml vắc xin đã hoàn nguyên, ở người lớn vào vùng cơ Delta ở cánh tay, trẻ nhỏ tiêm ở mặt trước – bên đùi. Không tiêm ở vùng mông.;Tiêm trong da: với liều 0.1ml vắc xin đã hoàn nguyên (bằng 1/5 liều tiêm bắp).",
                                           "Không được tiêm trong da ở những trường hợp sau: đang điều trị dài ngày bằng các thuốc ức chế miễn dịch (bao gồm cả corticoid), và thuốc Chloroquin; người bị khiếm khuyết miễn dịch; trẻ em hoặc người có vết cắn nặng phần đầu, cổ, hay đến khám trễ sau khi bị vết thương.",
                                           "Tại chỗ tiêm: đau, quầng đỏ, sưng, ngứa và nốt cứng.;Toàn thân: sốt, đau đầu, chóng mặt, mệt mỏi.",
                                           3, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C.",
                                           "Lịch tiêm vắc xin dại dự phòng trước phơi nhiễm gồm 3 mũi: Vào các ngày 0-7-21 hoặc (28).; Lịch tiêm khi xác định có phơi nhiễm:; Tiêm 4 mũi (*): vào các ngày N0 – N3 – N7 – N28;Tiêm 5 mũi (**) vào các ngày N0 – N3 – N7- N14 – N28; (*) Con vật sống sau 10 ngày theo dõi;(**) Con vật chết, bệnh, không theo dõi được;Người đã tiêm dự phòng trước phơi nhiễm hoặc sau phơi nhiễm ít nhất 3 mũi vắc xin Dại:Tiêm 2 mũi vào các ngày 0-3.\n",
                                           "Vắc xin Verorab (Pháp) phòng bệnh dại được chỉ định tiêm cho trẻ em và người lớn ở mọi lứa tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Vaxigrip Tetra (Pháp)",
                                           "vac-xin-vaxigrip-tetra.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("2-6 tháng", "4-6 tuổi", "9-18 tuổi",
                                                                 "Người trưởng thành")),
                                           "Vắc xin cúm Tứ giá Vaxigrip Tetra phòng được 4 chủng tuýp virus cúm gồm: 2 chủng cúm A (H1N1, H3N2) và 2 chủng cúm B (Yamagata, Victoria).",
                                           "Sanofi Pasteur (Pháp)",
                                           "Tiêm bắp hoặc tiêm dưới da.",
                                           "Quá mẫn cảm với các hoạt chất, với bất kỳ tá dược liệt kê trong mục “thành phần” hoặc bất kỳ chất nào có thể có trong thành phần dù với một lượng rất nhỏ còn sót lại (vết) như trứng (ovalbumin, protein của gà), neomycin, formaldehyde và octoxynol-9.;Hoãn tiêm vắc xin với những người bị sốt vừa hay sốt cao hay bị bệnh cấp tính.",
                                           "Phản ứng tại chỗ: ban đỏ (quầng đỏ), sưng, đau, bầm máu, nốt cứng.;Phản ứng toàn thân: sốt, khó chịu, run rẩy, mệt mỏi, đau đầu, đổ mồ hôi, đau khớp và đau cơ.",
                                           2, 1,
                                           "Vắc xin Vaxigrip Tetra được bảo quản ở nhiệt độ 2-8 độ C. Không để đông băng và tránh ánh sáng.",
                                           "Vắc xin Vaxigrip Tetra 0,5 ml dành cho trẻ từ 6 tháng tuổi đến dưới 9 tuổi chưa tiêm cúm có lịch tiêm 2 mũi:; Mũi 1: lần tiêm đầu tiên.;Mũi 2: cách mũi 1 ít nhất 4 tuần và tiêm nhắc hàng năm.;Từ 9 tuổi trở lên: Lịch tiêm 01 mũi duy nhất và nhắc lại hằng năm.",
                                           "Vắc xin Vaxigrip Tetra 0.5ml của Pháp phòng cúm mùa, được chỉ định cho trẻ từ 6 tháng tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Prevenar 13 (Bỉ)",
                                           "vac-xin-prevenar-13.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("2-6 tháng", "4-6 tuổi", "9-18 tuổi",
                                                                 "Người trưởng thành")),
                                           "Vắc xin Prevenar 13 là vắc xin phế cầu, phòng các bệnh phế cầu khuẩn xâm lấn gây nguy hiểm cho trẻ em và người lớn như viêm phổi, viêm màng não, nhiễm khuẩn huyết (nhiễm trùng máu), viêm tai giữa cấp tính,… do 13 chủng phế cầu khuẩn Streptococcus Pneumoniae gây ra (type 1, 3, 4, 5, 6A, 6B, 7F, 9V, 14, 18C, 19A, 19F và 23F). ",
                                           "Nghiên cứu và phát triển bởi Pfizer (Mỹ). Prevenar-13 được sản xuất tại Bỉ.",
                                           "Vắc xin Prevenar-13 được chỉ định tiêm bắp (vùng cơ delta) với liều 0.5ml",
                                           "Không dùng vắc xin Prevenar-13 trong thai kỳ.;Không tiêm vắc xin Prevenar-13 với người quá mẫn cảm với thành phần trong vắc xin hoặc với độc tố bạch hầu.",
                                           "Phản ứng tại chỗ tiêm: Nổi ban đỏ, chai cứng, sưng đau, tăng nhạy cảm tại chỗ tiêm.;Các phản ứng toàn thân khác: Sốt, đau đầu, buồn ngủ, giảm cảm giác thèm ăn, nôn mửa, tiêu chảy.",
                                           4, 1,
                                           "Bảo quản ở nhiệt độ lạnh (từ 2 – 8 độ C). Không được đóng băng.",
                                           "Từ 6 tuần tuổi đến dưới 7 tháng tuổi:  Lịch tiêm gồm 4 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: cách mũi 1 là 1 tháng.;Mũi 3: cách mũi 2 là 1 tháng.;Mũi 4 (mũi nhắc lại): tối thiểu 8 tháng kể từ mũi thứ 3;Từ 7 tháng đến dưới 12 tháng tuổi (chưa từng được tiêm phòng vắc xin trước đó):  Lịch tiêm gồm 3 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: cách mũi 1 là 1 tháng.;Mũi 3 (mũi nhắc lại): cách mũi 2 tối thiểu 6 tháng.;Từ 12 tháng đến dưới 24 tháng tuổi (chưa từng được tiêm phòng vắc xin trước đó):  Lịch tiêm gồm 2 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: cách mũi 1 tối thiểu 2 tháng.;Từ 24 tháng đến người lớn (chưa từng được tiêm phòng vắc xin trước đó hoặc chưa từng tiêm vắc xin Pneumo 23):  Lịch tiêm 01 mũi.",
                                           "Vắc xin Prevenar-13 (Bỉ) được chỉ định cho trẻ từ 6 tuần tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Boostrix (Bỉ)",
                                           "vac-xin-boostrix.jpg",
                                           new HashSet<>(Arrays.asList("9-18 tuổi", "Phụ nữ trước mang thai",
                                                                       "Người trưởng thành")),
                                           "Vắc xin Boostrix (Bỉ) tạo đáp ứng kháng thể chống 3 bệnh ho gà – bạch hầu – uốn ván.",
                                           "Glaxosmithkline (GSK) – Bỉ",
                                           "Vắc xin Boostrix được chỉ định tiêm bắp với liều 0.5ml. Không được tiêm dưới da hoặc tĩnh mạch. Ở trẻ lớn và người lớn, thường tiêm vào cơ delta còn trẻ nhỏ thường vào mặt trước – bên đùi.",
                                           "Tiền sử quá mẫn với thành phần của vắc xin. Quá mẫn sau khi tiêm vắc xin bạch hầu, ho gà hoặc uốn ván trước đó.;Người đã từng bị các biểu hiện về não: hôn mê, bất tỉnh, co giật kéo dài.;Người có tiền sử giảm tiểu cầu thoáng qua hoặc biến chứng thần kinh sau chủng ngừa bạch hầu và/hoặc uốn ván trước đó.",
                                           "Trẻ em 4-9 tuổi: Có thể chán ăn, ngủ gà gật, nhạy cảm chỗ tiêm, sốt, nôn, tiêu chảy.;Người lớn, thanh thiếu niên và trẻ em ≥10 tuổi: Có thể đau đầu, nhạy cảm chỗ tiêm, mệt mỏi, khó chịu chóng mặt, buồn nôn, rối loạn tiêu hóa.",
                                           1, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C. Không được để đông băng.",
                                           "Cho trẻ từ 4 tuổi trở lên và người lớn đã tiêm lịch cơ bản bạch hầu – ho gà – uốn ván:;Lịch tiêm 1 mũi;Tiêm nhắc 1 mũi mỗi 10 năm hoặc tiêm nhắc theo độ tuổi khuyến cáo;Cho trẻ từ 4 tuổi trở lên và người lớn chưa tiêm đủ lịch cơ bản bạch hầu – ho gà – uốn ván:;Lịch tiêm 3 mũi:;Mũi 1: Lần tiêm đầu tiên;Mũi 2: Một tháng sau mũi 1;Mũi 3: Sáu tháng sau mũi 2;Tiêm nhắc một mũi mỗi 10 năm hoặc tiêm nhắc theo độ tuổi khuyến cáo.",
                                           "Vắc xin Boostrix phòng ho gà – bạch hầu – uốn ván được chỉ định cho trẻ từ 4 tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Varilrix (Bỉ)",
                                           "vac-xin-varilrix.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("7-12 tháng", "9-18 tuổi", "Phụ nữ trước mang thai",
                                                                 "Người trưởng thành")),
                                           "Vắc xin Varilrix (Bỉ) là vắc xin sống giảm độc lực phòng bệnh thủy đậu do virus Varicella Zoster cho trẻ từ 9 tháng tuổi và người lớn chưa có miễn dịch.",
                                           "Glaxosmithkline (GSK) – Bỉ",
                                           "Vắc xin Varilrix được chỉ định tiêm dưới da ở vùng cơ delta hoặc vùng má ngoài đùi với liều 0.5ml.",
                                           "Hoãn tiêm vắc xin Varilrix với những người đang sốt cao cấp tính.;Người suy giảm miễn dịch tiên phát hoặc có số lượng tế bào lympho ít hơn 1.200/mm3.;Người thiếu hụt khả năng miễn dịch tế bào như: bạch hầu, ung thư bạch huyết, loạn tạo máu, nhiễm HIV.;Bệnh nhân đang điều trị ức chế miễn dịch (bao gồm việc sử dụng liều cao corticosteroid).;Chống chỉ định tiêm Varilrix cho người quá mẫn cảm với neomycin hoặc bất cứ thành phần khác có trong vắc xin.;Không tiêm vắc xin cho những người có dấu hiệu quá mẫn sau liều tiêm vắc xin thủy đậu trước đó.; Không dùng cho phụ nữ mang thai, tốt nhất nên hoàn thành phác đồ tiêm chủng 3 tháng trước khi mang thai.",
                                           "Phản ứng tại chỗ tiêm: Đau, sưng, nổi ban đỏ, chai cứng, ngứa, bầm tím, tăng nhạy cảm tại chỗ tiêm.;Các phản ứng toàn thân khác: Sốt, nhức đầu, mệt mỏi, chóng mặt, buồn nôn.",
                                           2, 2,
                                           "Vắc xin Varilrix được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C.",
                                           "Trẻ từ 09 tháng đến 12 tuổi có lịch tiêm 2 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: 3 tháng sau mũi 1.;Trẻ từ 13 tuổi trở lên và người lớn có lịch tiêm 2 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: cách mũi 1 ít nhất 1 tháng.",
                                           "Vắc xin Varilrix được chỉ định cho trẻ em từ 9 tháng tuổi và người lớn chưa có miễn dịch phòng bệnh thủy đậu. "));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Jeev (Ấn Độ)",
                                           "vac-xin-jeev.jpg",
                                           new HashSet<>(List.of("Ngoài các nhóm trên")),
                                           "Vắc xin JEEV là loại vắc xin tinh khiết, bất hoạt qua nuôi cấy từ tế bào Vero, giúp cơ thể tạo ra miễn dịch chủ động nhằm dự phòng bệnh viêm não Nhật Bản.",
                                           "Biological E. Limited – Ấn Độ ",
                                           "Vắc xin JEEV được chỉ định tiêm bắp;Tuyệt đối không tiêm vắc xin JEEV vào tĩnh mạch trong mọi trường hợp;Không được trộn lẫn vắc xin JEEV trong cùng một lọ hoặc ống tiêm với bất kỳ dung dịch hoặc loại vắc xin nào khác.",
                                           "Người bị dị ứng hoặc nghi ngờ dị ứng với các thành phần của vắc xin, kể cả các tá dược và chất tồn dư như Protamine Sulphate.;Trong trường hợp sau khi tiêm mũi 1 có biểu hiện quá mẫn, không nên tiêm mũi tiếp theo.;Người bị suy giảm miễn dịch bẩm sinh, nhiễm HIV/AIDS;Phụ nữ mang thai và đang cho con bú.",
                                           "Phản ứng tại chỗ tiêm: Sưng, đau, xuất hiện quầng đỏ tại vị trí tiêm, nhạy cảm đau,…;Các phản ứng toàn thân khác: Đau nhức các cơ, ban đỏ, sốt, khó chịu, buồn ngủ, mệt mỏi, trẻ quấy khóc, giảm sự thèm ăn, buồn nôn, …",
                                           2, 2,
                                           "Vắc xin JEEV được bảo quản trong tủ lạnh có nhiệt độ giao động từ 2 – 8°C. Không để đông lạnh, nếu bị đông đá, loại bỏ lọ vắc xin. Tránh ánh sáng mặt trời.",
                                           "Liều tiêm:;Trẻ từ 1 tuổi đến tròn 3 tuổi: tiêm liều 3mcg/0,5 ml;Người từ > 3 tuổi đến 49 tuổi: tiêm liều 6mcg/0,5 ml;Lịch tiêm:;02 mũi cơ bản: ;Mũi 1: Lần đầu tiêm trong độ tuổi;Mũi 2: Cách mũi 1 ít nhất 1 tháng;Các mũi tiêm nhắc: khuyến cáo cần tiêm mũi nhắc để tăng cường và duy trì miễn dịch. Mũi nhắc lại dựa trên tình hình dịch tễ bệnh viêm não Nhật Bản và khuyến cáo của các Quốc gia có cùng dịch tễ.",
                                           "Vắc xin JEEV được chỉ định tiêm chủng cho trẻ em từ tròn 12 tháng tuổi đến người lớn ≤ 49 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Gene Hbvax (Việt Nam)",
                                           "vac-xin-Gene-HBvax.jpg",
                                           new HashSet<>(List.of("Ngoài các nhóm trên")),
                                           "Vắc xin phòng viêm gan B tái tổ hợp Gene-HBvax phòng bệnh do virus viêm gan B – loại virus này có thể lây truyền qua đường máu, qua quan hệ tình dục và từ mẹ truyền sang con với khả năng lây nhiễm cao gấp 10 lần so với virus HIV. Một khi nhiễm virus viêm gan B người bệnh phải chấp nhận sống chung với bệnh suốt đời. 25% trong bệnh nhân viêm gan siêu vi B sẽ phát sinh những vấn đề nghiêm trọng về gan, kể cả ung thư gan.",
                                           "VABIOTECH (Việt Nam)",
                                           "Không được tiêm đường tĩnh mạch hoặc trong da, vắc xin Gene-HBvax được chỉ định tiêm bắp. Ở người lớn thì tiêm vắc xin vào vùng cơ delta, song ở trẻ sơ sinh và trẻ nhỏ thì nên tiêm vào vùng đùi ngoài thì tốt hơn vì cơ delta còn nhỏ. Ngoại lệ có thể tiêm vắc xin theo đường dưới da cho những bệnh nhân ưa chảy máu. Lắc kỹ lọ vắc xin trước khi tiêm.",
                                           "Những người mẫn cảm với bất cứ thành phần nào của vắc xin;Mắc các bệnh bẩm sinh;Mệt mỏi, sốt cao hoặc phản ứng toàn thân với bất kỳ một bệnh nhiễm trùng đang tiến triển;Bệnh tim, bệnh thận hoặc bệnh gan chưa điều trị ổn định;Bệnh tiểu đường chưa điều trị ổn định hoặc suy dinh dưỡng;Bệnh ung thư máu và các bệnh ác tính nói chung;Bệnh quá mẫn.",
                                           "Những phản ứng thường hay gặp nhất là đau, sưng và ban đỏ tại chỗ tiêm.;Những phản ứng toàn thân ít gặp như sốt, đau đầu, buồn nôn, chóng mặt và mệt mỏi cũng có thể xảy ra ở một vài người sau khi tiêm.",
                                           3, 3,
                                           "Bảo quản ở nhiệt độ lạnh (từ +2 đến +8 độ C). Không được đông băng.",
                                           "Trẻ sơ sinh và trẻ em đến tròn 10 tuổi: liều 10mcg/0.5ml;Trẻ lớn trên 10 tuổi và người lớn (10 tuổi 1 ngày): 20mcg/1ml; Lịch tiêm 0-1-2-12 tháng:;Mũi đầu tiên: lần tiêm đầu tiên. Đối với trẻ sơ sinh, tốt nhất tiêm trong vòng 24 giờ đầu sau sinh.;Mũi thứ hai: 1 tháng sau mũi đầu tiên;Mũi thứ ba: 2 tháng sau mũi đầu tiên;Mũi tiêm nhắc lại: 1 năm sau mũi đầu tiên.;Lịch tiêm 0-1-6 tháng:;Mũi đầu tiên: lần tiêm đầu tiên. Đối với trẻ sơ sinh, tốt nhất tiêm trong vòng 24 giờ đầu sau sinh;Mũi tiêm thứ hai: 1 tháng sau mũi tiêm đầu;Mũi tiêm thứ ba: 6 tháng sau mũi tiêm đầu;Mũi tiêm nhắc lại: 5 năm sau mũi tiêm đầu",
                                           "Vắc xin Gene-HBvax được chỉ định phòng viêm gan B cho trẻ sơ sinh và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Gardasil (Mỹ)",
                                           "vac-xin-gardasil.jpg",
                                           new HashSet<>(List.of("9-18 tuổi")),
                                           "Vắc xin Gardasil (Mỹ) phòng bệnh ung thư cổ tử cung, âm hộ, âm đạo, các tổn thương tiền ung thư và loạn sản, mụn cóc sinh dục, các bệnh lý do nhiễm virus HPV, được chỉ định dành cho trẻ em và phụ nữ trong độ tuổi từ 9-26 tuổi.",
                                           "Merck Sharp and Dohm (Mỹ)",
                                           "Tiêm bắp với liều 0.5ml vào vùng cơ Delta vào phần trên cánh tay hoặc phần trước bên của phía trên đùi.;Không được tiêm tĩnh mạch. Chưa có nghiên cứu về đường tiêm trong da hoặc dưới da nên không có khuyến cáo tiêm theo hai đường tiêm này",
                                           "Người mẫn cảm với các thành phần có trong vắc xin.;Không được tiếp tục dùng Gardasil nếu có phản ứng quá mẫn với lần tiêm trước.",
                                           "Phản ứng tại chỗ tiêm: sưng đau, có ban đỏ, hay gặp bầm tím và ngứa.;Rất hiếm gặp: co thắt khí quản nghiêm trọng.",
                                           3, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2-8 độ C. Không được đông băng và tránh ánh sáng. Khi đưa ra khỏi tủ bảo quản nên sử dụng vắc xin ngay nhưng cũng có thể để ngoài nhiệt độ phòng <25 độ C trong thời gian 3 ngày mà không bị ảnh hưởng đến chất lượng vắc xin. Sau 3 ngày, vắc xin cần được loại bỏ.",
                                           "Lịch tiêm của vắc xin Gardasil (Mỹ) 3 mũi:;Mũi 1: Lần tiêm đầu tiên.;Mũi 2: 2 tháng sau mũi 1.;Mũi 3: 6 tháng sau mũi 1.",
                                           "Vắc xin Gardasil (Mỹ) được chỉ định dành cho trẻ em gái và phụ nữ trong độ tuổi từ 9-26 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Influvac Tetra (Hà Lan)",
                                           "vac-xin-influvac-tetra.jpg",
                                           new HashSet<>(Arrays.asList("4-6 tuổi", "9-18 tuổi", "Người trưởng thành")),
                                           "Vắc xin Cúm Tứ giá Influvac Tetra được chỉ định để phòng ngừa bệnh cúm mùa do virus cúm thuộc hai chủng cúm A (H1N1, H3N2) và hai chủng cúm B (Yamagata, Victoria).",
                                           "Abbott – Hà Lan",
                                           "Tiêm bắp hoặc tiêm sâu dưới da.",
                                           "Người mẫn cảm với các thành phần hoạt tính, với bất kỳ tá dược hoặc bất cứ thành phần nào có thể chỉ có mặt với một lượng rất nhỏ như trứng (ovalbumin, protein gà), formaldehyde, cetyltrimethylammonium bromide, polysorbat 80, hoặc gentamicin.;Các bệnh nhân/trẻ em có triệu chứng sốt hoặc nhiễm trùng cấp tính sẽ phải hoãn tiêm chủng.",
                                           "Các phản ứng tại chỗ: đỏ, đau, sưng, cứng vị trí tiêm;Các phản ứng toàn thân: sốt, khó chịu, đầu đầu, đổ mồ hôi, chứng đau cơ, đau khớp;Các phản ứng bất lợi này thường mất đi trong vòng 1-2 ngày mà không cần điều trị.",
                                           2, 1,
                                           "Vắc xin cúm Influvac Tetra phải được bảo quản tại 2°C đến 8°C (trong tủ lạnh). Không được đông băng. Bảo quản trong bao bì gốc. Tránh ánh sáng.",
                                           "Vắc xin Influvac Tetra 0.5 ml dành cho trẻ từ 6 tháng tuổi đến dưới 9 tuổi chưa tiêm cúm có lịch tiêm 2 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: cách mũi 1 ít nhất 4 tuần và tiêm nhắc hàng năm.;Từ 9 tuổi trở lên: Lịch tiêm 01 mũi duy nhất và nhắc lại hằng năm.",
                                           "Vắc xin Influvac Tetra 0.5ml được chỉ định cho trẻ từ 6 tháng tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Synflorix (Bỉ)",
                                           "Synflorix-1.jpg",
                                           new HashSet<>(Arrays.asList("2-6 tháng", "4-6 tuổi", "9-18 tuổi")),
                                           "Vắc xin Synflorix phòng tránh 10 chủng vi khuẩn phế cầu (Streptococcus pneumoniae) gây các bệnh như: Hội chứng nhiễm trùng, viêm màng não, viêm phổi, nhiễm khuẩn huyết và viêm tai giữa cấp,…",
                                           "Glaxosmithkline (GSK) – Bỉ",
                                           "Vắc xin Synflorix tiêm bắp ở mặt trước – bên đùi của trẻ nhỏ và tiêm ở cơ delta cánh tay của trẻ lớn. Không được tiêm tĩnh mạch hoặc tiêm trong da đối với vắc xin Synflorix.",
                                           "Synflorix không được tiêm cho các đối tượng quá mẫn với bất kỳ thành phần nào trong vắc xin.",
                                           "Tại chỗ tiêm: sưng, đau, đỏ.; Toàn thân: trẻ có thể sốt trên 38 độ C, ăn uống kém, bị kích thích, quấy khóc.",
                                           3, 0,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C, không được để đông băng.",
                                           "Vắc xin phòng bệnh do phế cầu dành cho trẻ từ 6 tuần đến 6 tháng tuổi có lịch tiêm:;Mũi 1: vào 2 tháng tuổi.;Mũi 2: vào 3 tháng tuổi.;Mũi 3: vào 4 tháng tuổi.;Mũi nhắc lại: sau 6 tháng kể từ mũi thứ 3.;Vắc xin phòng bệnh do phế cầu dành cho trẻ từ 7-11 tháng tuổi (chưa từng được tiêm phòng vắc xin trước đó) có lịch tiêm:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: cách mũi 1 là 1 tháng.;Mũi nhắc lại: vào năm tuổi thứ 2 và cách mũi 2 ít nhất là 2 tháng.; Vắc xin phòng bệnh do phế cầu dành cho trẻ từ 12 tháng đến trước sinh nhật lần thứ 6 (chưa từng được tiêm phòng vắc xin trước đó) có lịch tiêm:; Mũi 1: lần tiêm đầu tiên.;Mũi 2: 2 tháng sau mũi 1.",
                                           "Vắc xin Synflorix (Bỉ) được chỉ định cho trẻ từ 6 tuần tuổi trở lên và trước sinh nhật lần thứ 6."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Infanrix Hexa (Bỉ)",
                                           "vac-xin-Infanrix-hexa.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Vắc xin Infanrix Hexa là vắc xin kết hợp phòng được 6 loại bệnh trong 1 mũi tiêm, bao gồm: Ho gà, bạch hầu, uốn ván, bại liệt, viêm gan B và các bệnh viêm phổi, viêm màng não mủ do H.Influenzae týp B (Hib). Tích hợp trong duy nhất trong 1 vắc xin, 6 trong 1 Infanrix Hexa giúp giảm số mũi tiêm, đồng nghĩa với việc hạn chế đau đớn cho bé khi phải tiêm quá nhiều mũi.",
                                           "Glaxosmithkline (GSK) – Bỉ",
                                           "Infanrix Hexa được chỉ định tiêm bắp sâu. Không được tiêm tĩnh mạch hoặc trong da.",
                                           "Chống chỉ định đối với những trường hợp mẫn cảm với các hoạt chất hay bất cứ tá dược hoặc chất tồn dư nào trong thuốc.;Quá mẫn sau mũi tiêm vắc xin bạch hầu, uốn ván, ho gà, viêm gan B, bại liệt hoặc Hib trước đó.;Infanrix hexa chống chỉ định đối với những trẻ trong tiền sử đã có bệnh về não không rõ nguyên nhân trong vòng 7 ngày sau khi tiêm vắc xin chứa thành phần ho gà.",
                                           "Tại chỗ tiêm: sưng đỏ, đau từ 1 – 3 ngày. Có thể nổi cục cứng, sau khoảng 1-3 tuần sẽ tự khỏi;Toàn thân: Trẻ có thể sốt, quấy khóc, nôn, tiêu chảy, bú kém",
                                           4, 0,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 – 8 độ C. Không đông đá huyền dịch DTPa-HB-IPV và vắc xin đã hoàn nguyên. Loại bỏ nếu vắc xin bị đông băng.",
                                           "Lịch tiêm 6 trong 1 Infanrix Hexa bao gồm 4 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: 1 tháng sau mũi 1.;Mũi 3: 1 tháng sau mũi 2.;Mũi 4: cách mũi thứ 3 là 12 tháng (cách tối thiểu 6 tháng).",
                                           "Vắc xin Infanrix Hexa được chỉ định tiêm cho trẻ từ 6 tuần tuổi đến 2 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Hexaxim (Pháp)",
                                           "vac-xin-hexaxim-2.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Vắc xin Hexaxim là vắc xin kết hợp phòng được 6 loại bệnh trong 1 mũi tiêm, bao gồm: Ho gà, bạch hầu, uốn ván, bại liệt, viêm gan B và các bệnh viêm phổi, viêm màng não mủ do H.Influenzae týp B (Hib). Tích hợp trong duy nhất trong 1 vắc xin, 6 trong 1 Hexaxim giúp giảm số mũi tiêm, đồng nghĩa với việc hạn chế đau đớn cho bé khi phải tiêm quá nhiều. ",
                                           "Sanofi Pasteur (Pháp)",
                                           "Hexaxim được chỉ định tiêm bắp. Vị trí tiêm là mặt trước – ngoài của phần trên đùi và vùng cơ delta ở trẻ 15 tháng tuổi trở lên.",
                                           "Tiền sử trước đây bị phản ứng phản vệ sau khi tiêm Hexaxim.;Quá mẫn với các hoạt chất hay bất cứ tá dược nào được liệt kê trong thành phần của vắc xin, với các dư lượng vết (glutaraldehyde (1), formaldehyde (2), neomycin, streptomycin, polymyxin B), với vắc xin ho gà bất kỳ, hoặc trước đây từng bị phản ứng quá mẫn sau khi tiêm Hexaxim hoặc sau khi tiêm vắc xin chứa các thành phần tương tự .;Đối tượng có bệnh lý não (tổn thương ở não) không rõ nguyên nhân, xảy ra trong vòng 7 ngày sau khi tiêm 1 vắc xin chứa thành phần ho gà (vắc xin ho gà vô bào hay nguyên bào). Trong trường hợp này, nên ngừng tiêm vắc xin ho gà và có thể tiếp tục với quá trình tiêm chủng với các vắc xin Bạch hầu, Uốn ván, Viêm gan B, Bại liệt và Hib.;Không nên tiêm vắc xin ho gà cho người có rối loạn thần kinh không kiểm soát hoặc động kinh không kiểm soát cho đến khi bệnh được điều trị, bệnh ổn định và lợi ích rõ ràng vượt trội nguy cơ.;Trẻ bị dị ứng với một trong các thành phần của vắc xin hay với vắc xin ho gà (vô bào hoặc nguyên bào), hay trước đây trẻ đã có phản ứng dị ứng sau khi tiêm vắc xin có chứa các chất tương tự.;Trẻ có bệnh não tiến triển hoặc tổn thương ở não.",
                                           "Tại chỗ tiêm: sưng đỏ, đau từ 1 – 3 ngày. Có thể nổi cục cứng, sau khoảng 1-3 tuần sẽ tự khỏi;Toàn thân: Trẻ có thể sốt, quấy khóc, nôn, tiêu chảy, bú kém.",
                                           4, 0,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C.",
                                           "Lịch tiêm 6 trong 1 Hexaxim bao gồm 4 mũi:;Mũi 1: lần tiêm đầu tiên;Mũi 2: 1 tháng sau mũi 1;Mũi 3: 1 tháng sau mũi 2;Mũi 4: cách mũi thứ 3 là 12 tháng (cách tối thiểu 6 tháng).",
                                           "Vắc xin Hexaxim được chỉ định tiêm cho trẻ từ 6 tuần tuổi đến 2 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Pentaxim (Pháp)",
                                           "vac-xin-pentaxim-1.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Vắc xin Pentaxim là vắc xin kết hợp phòng được 5 loại bệnh trong 1 mũi tiêm, bao gồm: Ho gà, bạch hầu, uốn ván, bại liệt và các bệnh viêm phổi, viêm màng não mủ do H.Influenzae týp B (Hib). Tích hợp trong duy nhất trong 1 vắc xin, 5 trong 1 Pentaxim giúp giảm số mũi tiêm, đồng nghĩa với việc hạn chế đau đớn cho bé khi phải tiêm quá nhiều.",
                                           "Sanofi Pasteur (Pháp)",
                                           "Vắc xin Pentaxim được chỉ định tiêm bắp (ở mặt trước – bên đùi).",
                                           "Chống chỉ định đối với những trường hợp mẫn cảm với các hoạt chất hay bất cứ tá dược nào trong thuốc.;Trẻ bị dị ứng với một trong các thành phần của vắc xin hay với vắc xin ho gà (vô bào hoặc nguyên bào), hay trước đây trẻ đã có phản ứng dị ứng sau khi tiêm vắc xin có chứa các chất tương tự.;Trẻ có bệnh não tiến triển hoặc tổn thương ở não.;Nếu lần trước trẻ từng bị bệnh não (tổn thương ở não) trong vòng 7 ngày sau khi tiêm vắc xin ho gà (ho gà vô bào hay nguyên bào).",
                                           "Tại chỗ tiêm: nốt quầng đỏ, nốt cứng. Các triệu chứng trên thường gặp trong 48 giờ sau khi tiêm và có thể kéo dài 48 – 72 giờ.;Toàn thân: trẻ có thể sốt, quấy khóc, tiêu chảy, nôn, chán ăn, buồn ngủ, phát ban.",
                                           4, 0,
                                           "Bảo quản ở nhiệt độ lạnh (từ 2 – 8 độ C). Không được đóng băng. Vắc xin phải được hoàn nguyên trước khi tiêm, tạo nên hỗn dịch màu trắng đục. Sau khi hoàn nguyên nên sử dụng ngay.",
                                           "Lịch tiêm vắc xin 5 trong 1 Pentaxim gồm 4 mũi:;Mũi 1: lần tiêm đầu tiên;Mũi 2: 1 tháng sau mũi 1;Mũi 3: 1 tháng sau mũi 2;Mũi 4: 1 năm sau mũi 3",
                                           "Vắc xin Pentaxim được chỉ định tiêm cho trẻ từ 2 tháng tuổi đến tròn 2 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Heberbiovac (Cu ba)",
                                           "vac-xin-Heberbiovac-HB.jpg",
                                           new HashSet<>(Arrays.asList("0-2 tháng", "2-6 tháng", "7-12 tháng",
                                                                       "13-24 tháng", "Phụ nữ trước mang thai")),
                                           "Vắc xin Heberbiovac HB là vắc xin viêm gan B tái tổ hợp phòng bệnh do virus viêm gan B – loại virus có thể lây truyền dễ dàng qua đường máu, quan hệ tình dục và từ mẹ truyền sang con.",
                                           "Center for Genetic Engineering and Biotechnology (C.I.G.B) – Cuba.",
                                           "Vắc xin Heberbiovac HB được chỉ định tiêm vào bắp sâu vùng trước bên đùi của trẻ sơ sinh và trẻ dưới 1 tuổi, hoặc vùng cơ delta của trẻ trên 1 tuổi và người lớn. Không tiêm vắc xin theo những đường khác.",
                                           "Không tiêm vắc xin cho những đối tượng đang bị sốt cao do nhiễm trùng nặng hoặc quá mẫn với một trong các thành phần có trong vắc xin.",
                                           "Phản ứng tại chỗ tiêm: Đau, sưng, nổi ban đỏ, chai cứng, ngứa, bầm tím, tăng nhạy cảm tại chỗ tiêm.;Các phản ứng toàn thân khác: Sốt, nhức đầu, mệt mỏi, chóng mặt, buồn nôn.",
                                           3, 3,
                                           "Vắc xin Heberbiovac HB được bảo quản trong tủ lạnh (2°C – 8°C). Không để đông đá.",
                                           "Vắc xin Heberbiovac HB phòng bệnh viêm gan B có lịch tiêm 3 mũi:;Mũi 1: lần tiêm đầu tiên;Mũi 2: 1 tháng sau mũi 1;Mũi 3: 6 tháng sau mũi 1;Phác đồ tiêm nhanh vắc xin Heberbiovac HB:;Mũi 1: lần tiêm đầu tiên;Mũi 2: 1 tháng sau mũi 1;Mũi 3: 2 tháng sau mũi 1;Mũi 4: 12 tháng sau mũi 1;Phác đồ tiêm vắc xin Heberbiovac HB cho người suy giảm miễn dịch, suy thận, thẩm phân phúc mạc:;Mũi 1: lần tiêm đầu tiên;Mũi 2: 1 tháng sau mũi 1;Mũi 3: 2 tháng sau mũi 1;Mũi 4: 6 tháng sau mũi 1",
                                           "Vắc xin Heberbiovac HB được chỉ định tiêm chủng cho trẻ sơ sinh và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Priorix (Bỉ)",
                                           "vac-xin-priorix.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("7-12 tháng", "13-24 tháng", "4-6 tuổi", "9-18 tuổi",
                                                                 "Phụ nữ trước mang thai", "Người trưởng thành")),
                                           "Vắc xin Priorix có thể tiêm sớm cho trẻ từ 9 tháng tuổi, Priorix có thể tăng khả năng bảo vệ lên đến 98% nếu tiêm đủ 2 mũi. Priorix bảo vệ sớm cho trẻ, giảm tỷ lệ bệnh nặng và tử vong, giúp ngăn ngừa sự lây lan của virus.",
                                           "Glaxosmithkline (GSK) – Bỉ",
                                           "Vắc xin Priorix được chỉ định tiêm dưới da. Có thể tiêm bắp vắc xin Priorix ở vùng cơ delta hoặc mặt trước bên đùi.;Tiêm vắc xin Priorix dưới da cho những đối tượng bị rối loạn chảy máu.",
                                           "Chống chỉ định tiêm Priorix cho những người có tiền sử quá mẫn hệ thống với neomycin hoặc với bất kỳ thành phần nào của vắc xin. Không chống chỉ định khi có tiền sử viêm da tiếp xúc với neomycin.;Chống chỉ định tiêm, Priorix cho các đối tượng có dấu hiệu quá mẫn sau khi tiêm liều vắc xin Sởi, Quai bị và/hoặc Rubella trước đó.;Chống chỉ định tiêm Priorix cho những người suy giảm miễn dịch dịch thể hoặc tế bào (nguyên phát hoặc mắc phải), như nhiễm HIV có triệu chứng.;Chống chỉ định tiêm Priorix cho phụ nữ mang thai. Nên tránh có thai trong 1 tháng sau khi tiêm vắc xin.",
                                           "Phản ứng tại chỗ tiêm: Đau, sưng, nổi ban đỏ, chai cứng, ngứa, bầm tím, tăng nhạy cảm tại chỗ tiêm.;Các phản ứng toàn thân khác: Sốt, nhức đầu, mệt mỏi, chóng mặt, buồn nôn.",
                                           3, 2,
                                           "Vắc xin Priorix được bảo quản trong tủ lạnh (2°C – 8°C). Không làm đông đá vắc xin, đông khô cũng như dung môi.Có thể bảo quản dung môi trong tủ lạnh hoặc nhiệt độ phòng. Bảo quản trong bao bì gốc để tránh ánh sáng.",
                                           "Trẻ em từ 9 tháng tuổi đến dưới 12 tháng tuổi tại thời điểm tiêm lần đầu tiên (chưa tiêm vắc xin Sởi hay MMR II);Mũi 1: lần tiêm đầu tiên trong độ tuổi.;Mũi 2: cách mũi 1 là 3 tháng.;Mũi 3: cách mũi 2 là 3 năm hoặc hẹn lúc 4-6 tuổi.;Trẻ em từ 12 tháng tuổi đến dưới 7 tuổi;Mũi 1: lần tiêm đầu tiên trong độ tuổi.;Mũi 2: cách mũi 1 là 3 tháng.;Trẻ em từ 7 tuổi và người lớn;Mũi 1: lần tiêm đầu tiên trong độ tuổi.;Mũi 2: cách mũi 1 là 1 tháng.",
                                           "Vắc xin Priorix được chỉ định tiêm chủng cho trẻ em từ 9 tháng tuổi và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Rotarix (Bỉ)",
                                           "vac-xin-rotarix.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Rotarix là vắc xin sống, giảm độc lực được chỉ định cho trẻ từ 6 tuần tuổi phòng viêm dạ dày – ruột do Rotavirus tuýp huyết thanh G1 và không phải G1. Mặc dù trong thành phần chỉ có 1 tuýp G1P tuy nhiên vắc xin có khả năng bảo vệ chéo tất cả các tuýp G1 và không phải G1 (G2, G3, G4, G9).",
                                           "GlaxoSmithKline (Bỉ)",
                                           "Chỉ dùng đường uống.;Vắc xin Rotarix có khả năng bám dính rất tốt vì vậy sau khi uống nếu trẻ có nôn trớ thì cũng không cần uống liều khác. Tuy nhiên nếu xác định rằng đã bị nôn trớ phần lớn vắc xin thì có thể uống lại.",
                                           "Không dùng vắc xin Rotarix cho trẻ đã quá mẫn cảm ở lần uống đầu tiên hoặc với bất kỳ thành phần nào của vắc xin.;Không dùng cho trẻ có dị tật bẩm sinh về đường tiêu hóa vì có thể dẫn đến lồng ruột (như túi thừa Meckel).",
                                           "Toàn thân: rối loạn tiêu hóa và thường tự khỏi sau vài ngày.;Nếu đi ngoài phân nước nhiều lần, nôn nhiều, có dấu hiệu mất nước nên khám lại ngay tại cơ sở y tế.",
                                           2, 2,
                                           "Vắc xin đông khô được bảo quản ở nhiệt độ 2-8 độ C, tránh ánh sáng. Dung môi hoàn nguyên có thể bảo quản ở 2-8 độ C hoặc ở nhiệt độ phòng (<37 độ C). Sau khi hoàn nguyên, vắc xin được sử dụng ngay hoặc bảo quản trong tủ lạnh từ 2-8 độ C trong vòng 24 giờ. Sau 24 giờ phải loại bỏ vắc xin đã hoàn nguyên.",
                                           "Vắc xin Rotarix (Bỉ) có lịch uống 2 liều liên tiếp cách nhau tối thiểu 4 tuần.;Liều đầu tiên có thể uống sớm lúc 6 tuần tuổi.;Cần hoàn thành phác đồ đến tròn 24 tuần tuổi (Muộn nhất là đến tròn 6 tháng tuổi).",
                                           "Vắc xin Rotarix được chỉ định chủng ngừa cho trẻ từ 6 tuần tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Rotateq (Mỹ)",
                                           "vac-xin-RotaTeq.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Rotateq là vắc xin sống giảm độc lực phòng ngừa Rotavirus, ngũ giá, dùng đường uống được chỉ định cho trẻ từ 7,5 tuần tuổi phòng viêm dạ dày – ruột do Rotavirus ở trẻ nhỏ gây ra bởi các tuýp vi-rút G1, G2, G3, G4 và các tuýp có chứa P1A (ví dụ như G9).",
                                           "Meck Sharp and Dohme (MSD) – Mỹ.",
                                           "Chỉ dùng đường uống. Không được tiêm.;Nếu trẻ bị nôn trớ hoặc nhổ ra thì không được uống liều thay thế vì chưa có nghiên cứu lâm sàng cho việc uống thay thế. Cứ dùng liều tiếp theo trong lịch uống vắc xin.;Vắc xin được đóng trong tuýp định liều có thể vặn nắp và cho uống luôn, không được pha loãng bằng nước hoặc sữa.",
                                           "Không dùng vắc xin cho trẻ khi có dị ứng với bất kỳ thành phần nào trong vắc xin.;Không dùng liều tiếp theo nếu trẻ có biểu hiện mẫn cảm với lần uống vắc xin Rotateq trước.;Không dùng vắc xin Rotateq cho trẻ suy giảm miễn dịch kết hợp trầm trọng, vì đã có báo cáo về trường hợp viêm dạ dày ruột khi dùng vắc xin ở trẻ suy giảm miễn dịch kết hợp trầm trọng.",
                                           "Toàn thân: rối loạn tiêu hóa và thường tự khỏi sau vài ngày.;Nếu đi ngoài phân nước nhiều lần, nôn nhiều, có dấu hiệu mất nước nên khám lại ngay tại cơ sở y tế.",
                                           3, 3,
                                           "Vắc xin Rotateq được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C. Sau khi lấy ra khỏi tủ lạnh, vắc xin cần được sử dụng ngay. Khi bảo quản ở nhiệt độ 25 độ C, vắc xin Rotateq có thể sử dụng trong vòng 48 giờ. Sau 48 giờ vắc xin cần phải loại bỏ theo quy định.",
                                           "Vắc xin Rotateq (Mỹ) có lịch uống 3 liều, các liều cách nhau tối thiểu 4 tuần:;Liều đầu tiên khi trẻ được 7.5 tuần đến tròn 12 tuần tuổi.;Cần hoàn thành phác đồ muộn nhất đến trước 32 tuần.",
                                           "Vắc xin Rotateq được chỉ định chủng ngừa cho trẻ từ 6 tuần tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Rotavin (Việt Nam)",
                                           "vac-xin-rotavin-1.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Vắc xin Rotavin là vắc xin sống giảm độc lực có tác dụng phòng nguy cơ nhiễm virus Rota – nguyên nhân gây tình trạng tiêu chảy cấp ở trẻ nhỏ. Vắc xin có dung dịch màu hồng, được sản xuất trên dây chuyền công nghệ hiện đại, đạt các tiêu chuẩn của Tổ chức Y tế Thế giới (WHO) về vắc xin uống.",
                                           "Polyvac – Việt Nam.",
                                           "Chỉ được dùng đường uống, không được tiêm.; Liều uống: 2ml/liều.",
                                           "Không dùng Rotavin cho trẻ quá mẫn sau khi uống liều vắc xin đầu tiên, hoặc quá mẫn với bất cứ thành phần nào của thuốc.;Không sử dụng vắc xin Rotavin nếu trẻ có bệnh lý nặng, cấp tính, sốt cao, trẻ đang bị tiêu chảy hoặc nôn.;Không sử dụng Rotavin cho trẻ bị dị tật bẩm sinh đường tiêu hóa, bị lồng ruột hay đang bị suy giảm miễn dịch nặng.;Vắc xin không dự kiến sử dụng ở người lớn. Không có chỉ định sử dụng vắc xin ở phụ nữ có thai và cho con bú.;Việc cho con bú không làm giảm hiệu quả của vắc xin, nên vẫn có thể cho con bú sau khi trẻ đã sử dụng vắc xin.",
                                           "Toàn thân: rối loạn tiêu hóa và thường tự khỏi sau vài ngày.;Nếu đi ngoài phân nước nhiều lần, nôn nhiều, có dấu hiệu mất nước nên khám lại ngay tại cơ sở y tế.",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C.",
                                           "Vắc xin Rotavin có lịch uống 2 liều liên tiếp cách nhau tối thiểu 4 tuần:;Liều đầu tiên có thể uống sớm lúc 6 tuần tuổi.;Cần hoàn thành phác đồ muộn nhất đến trước 6 tháng.",
                                           "Vắc xin Rotavin được chỉ định chủng ngừa cho trẻ từ 6 tuần tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin BCG (Việt Nam)",
                                           "vac-xin-bcg.jpg",
                                           new HashSet<>(List.of("0-2 tháng")),
                                           "Vắc xin BCG phòng ngừa hiệu quả các hình thái lao nguy hiểm, trong đó có lao viêm màng não với độ bảo vệ lên tới 70%. Vắc xin được khuyến cáo cho trẻ sơ sinh và trẻ nhỏ, chỉ cần tiêm 1 liều duy nhất có thể bảo vệ trọn đời, không cần tiêm nhắc lại.",
                                           "Việt Nam.",
                                           "Tiêm trong da chính xác, ở mặt ngoài phía trên cánh tay hoặc vai trái.;Nhân viên y tế cần sử dụng bơm kim tiêm riêng để tiêm vắc xin BCG.",
                                           "Không tiêm vắc xin phòng lao cho trẻ có dấu hiệu hoặc triệu chứng bệnh AIDS.;Các trường hợp chống chỉ định khác theo hướng dẫn của nhà sản xuất vắc xin lao.",
                                           "Tại chỗ tiêm: đau, sưng, nóng.;Toàn thân: Trẻ sốt nhẹ, quấy khóc, bú kém, thường hết sau một vài ngày.",
                                           1, 1,
                                           "Vắc xin BCG được bảo quản ở nhiệt độ 2-8 độ C (vắc xin không bị hỏng bởi đông băng nhưng dung môi thì không được đông băng). Sau khi hoàn nguyên dung dịch tiêm cần được bảo quản ở nhiệt độ 2-8 độ C trong vòng 6 giờ. Phần còn lại của lọ vắc xin sau mỗi buổi tiêm chủng hoặc sau 6 giờ cần phải hủy bỏ.",
                                           "Vắc xin phòng bệnh Lao (BCG) chỉ tiêm 1 mũi duy nhất, không cần tiêm nhắc lại.",
                                           "Vắc xin Lao (BCG) được chỉ định tiêm cho trẻ có cân nặng từ 2.000 gram trở lên và tiêm càng sớm càng tốt trong 30 ngày sau khi sinh."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Mengoc BC (Cu ba)",
                                           "vac-xin-va-mengoc-bc.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Vắc xin VA-Mengoc-BC phòng bệnh viêm màng não do não mô cầu khuẩn Meningococcal tuýp B và C gây ra.",
                                           "Finlay Institute (Cu Ba)",
                                           "Tiêm bắp sâu, tốt nhất là vào vùng cơ delta cánh tay. Tuy nhiên ở trẻ nhỏ có thể tiêm bắp đùi, ở mặt trước ngoài của đùi.;Không được tiêm tĩnh mạch.",
                                           "Người quá mẫn với các thành phần của vắc xin.;Người đang sốt, nhiễm khuẩn cấp tính, dị ứng đang tiến triển.;Hiếm khi có phản ứng dị ứng nhưng cần ngưng liều thứ 2 nếu liều 1 có dấu hiệu dị ứng.",
                                           "Ít gặp: tại vết tiêm có đau, nổi ban đỏ hoặc sưng nhẹ. Những triệu chứng này thường biến mất sau 72 giờ.;Toàn thân: có thể có sốt nhẹ, hoặc cảm giác khó chịu, đau đầu, buồn ngủ.",
                                           2, 2,
                                           "Vắc xin cần được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C và không được đông băng.",
                                           "Vắc xin VA-Mengoc-BC có lịch tiêm 2 mũi cách nhau 45 ngày.",
                                           "Vắc xin phòng viêm màng não do mô cầu BC của CuBa được chỉ định tiêm cho trẻ từ 6 tháng tuổi trở lên và người lớn đến 45 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Ivacflu-S (Việt Nam)",
                                           "vac-xin-ivacflu-s.jpg",
                                           new HashSet<>(List.of("Người trưởng thành")),
                                           "Vắc xin Ivacflu – S phòng 3 chủng cúm A(H3N2), cúm A(H1N1),và cúm B (Victoria/Yamagata).",
                                           "Viện Vắc xin và Sinh phẩm Y tế IVAC – Việt Nam",
                                           "Vắc xin Ivacflu-S được sử dụng qua đường tiêm bắp. Vị trí tiêm: Cơ delta (bắp cánh tay). Không được tiêm vắc xin vào mạch máu.",
                                           "Có tiền sử sốc phản vệ với vắc xin cúm IVACFLU-S.;Tiền sử mẫn cảm với bất cứ chủng virus cúm nào trong thành phần vắc xin.;Tiền sử mẫn cảm với cao su (của nút lọ đựng vắc xin) hoặc các thành phần pha chế vắc xin như dung dịch PBS.;Người có hội chứng Guilain-Barre, có rối loạn thần kinh.;Người bị động kinh đang tiến triển hoặc có tiền sử co giật.;Người có cơ địa mẫn cảm nặng với các vắc xin khác (đã từng bị sốc phản vệ khi tiêm vắc xin);Hoãn tiêm chủng nếu người tiêm có tình trạng bệnh lý mà cán bộ tiêm chủng nhận thấy không an toàn khi tiêm vắc xin (sốt trên 38oC; bệnh nhiễm trùng cấp tính…) hoặc không đảm bảo hiệu quả của vắc xin (đang dùng thuốc ức chế miễn dịch trên 14 ngày, mắc lao thể hoạt động…).",
                                           "Tại chỗ tiêm: đau, đỏ, sưng;Toàn thân: sốt,mệt mỏi, đau đầu",
                                           1, 1,
                                           "Nhiệt độ bảo quản vắc xin từ 2 đến 8 độ C, tránh đông băng. Bảo quản vắc xin nguyên trong hộp để tránh ánh sáng.",
                                           "Vắc xin Ivacflu – S 0,5ml (Việt Nam) có dành cho người từ 18 tuổi đến sinh nhật lần thứ 60 có lịch tiêm 1 mũi. Tiêm nhắc lại 01 mũi hàng năm hoặc vào đầu các mùa có nguy cơ bùng phát dịch.",
                                           "Vắc xin dành cho người lớn từ 18 tuổi đến 60 tuổi (đến sinh nhật lần thứ 60 tuổi). Lưu ý, không chỉ định tiêm cho phụ nữ mang thai."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Mvvac (Việt Nam)",
                                           "vac-xin-MVVAC.jpg",
                                           new HashSet<>(List.of("7-12 tháng")),
                                           "Vắc xin MVVAC tạo miễn dịch chủ động phòng bệnh sởi cho trẻ từ 9 tháng tuổi trở lên và người chưa có kháng thể sởi.",
                                           " Polyvac – Việt Nam",
                                           "Vắc xin MVVAC chỉ được tiêm dưới da, không được tiêm tĩnh mạch.",
                                           "Trường hợp mẫn cảm với bất cứ thành phần nào của vắc xin;Phụ nữ có thai;Trường hợp bị nhiễm trùng cấp tính đường hô hấp, mắc bệnh lao tiến triển chưa được điều trị;Người bị suy giảm miễn dịch (trừ trẻ em bị HIV);Người bị bệnh ác tính.",
                                           "Tại chỗ tiêm: đau, đỏ, sưng;Toàn thân: Sốt, ban, ho và sổ mũi",
                                           3, 3,
                                           "Lọ vắc xin sởi dạng đông khô được bảo quản ở khoảng nhiệt độ  ≤ 8 độ C và tránh ánh sáng. Lọ nước pha tiêm được bảo quản nhiệt độ dưới 30oC, không được làm đông băng. Lọ vắc xin sau khi pha hồi chỉnh bằng nước pha tiêm sẽ được bảo quản ở nhiệt độ 2 độ C đến 8 độ C và chỉ sử dụng trong vòng 6 giờ.",
                                           "Liều thứ 1 tiêm cho trẻ từ 9 tháng tuổi trở lên;;Liều thứ 2 (vắc xin phối hợp Sởi – Quai bị – Rubella): được tiêm cho trẻ từ 12 tháng tuổi (cách mũi sởi đơn ít nhất 1 tháng);;Liều thứ 3: 3 năm sau hoặc lúc trẻ 4-6 tuổi.",
                                           "Vắc xin MVVAC phòng bệnh sởi cho trẻ từ 9 tháng tuổi trở lên và người chưa có kháng thể sởi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin MMR II (Mỹ)",
                                           "vac-xin-MMR-II.jpg",
                                           new HashSet<>(Arrays.asList("7-12 tháng", "4-6 tuổi", "9-18 tuổi",
                                                                       "Phụ nữ trước mang thai", "Người trưởng thành")),
                                           "Vắc xin phối hợp MMR-II của Mỹ là vắc xin sống giảm độc lực tạo miễn dịch chủ động dùng để ngăn ngừa nhiễm virus bệnh sởi, quai bị và rubella. Thuốc hoạt động bằng cách giúp cơ thể tạo kháng thể chống lại virus.",
                                           "Merck Sharp and Dohm (Mỹ)",
                                           "Tiêm dưới da, không được tiêm tĩnh mạch.",
                                           "Người dị ứng với bất kỳ thành phần nào trong vắc xin, kể cả gelatin.;Người đang mang thai, phải tránh mang thai 3 tháng sau khi tiêm vắc xin cho phụ nữ.;Có tiền sử dị ứng với neomycin.;Đang có bệnh lý sốt hoặc viêm đường hô hấp.;Bệnh lao đang tiến triển mà chưa được điều trị hoặc người đang điều trị bằng thuốc ức chế miễn dịch.;Người có rối loạn về máu, bệnh bạch cầu hay u hạch bạch huyết; hoặc ở người có những khối u tân sinh ác tính ảnh hưởng tới tủy xương hoặc hệ bạch huyết.;Người bị bệnh suy giảm miễn dịch tiên phát hoặc thứ phát, bao gồm cả người mắc bệnh AIDS và người có biểu hiện lâm sàng về nhiễm virus gây suy giảm miễn dịch; các bệnh gây giảm hoặc bất thường gammaglobulin máu.;Người có tiền sử trong gia đình suy giảm miễn dịch bẩm sinh hoặc di truyền cho đến khi chứng minh được họ có khả năng đáp ứng miễn dịch với vắc xin.",
                                           "Tại chỗ tiêm: đau, đỏ, sưng;Toàn thân: Sốt, ban, ho và sổ mũi",
                                           2, 2,
                                           "Trước khi hoàn nguyên, vắc xin cần được bảo quản ở nhiệt độ 2-8 độ C và tránh ánh sáng. Sau khi hoàn nguyên nên sử dụng ngay vắc xin,có thể sử dụng được vắc xin đã hoàn nguyên nếu được bảo quản ở chỗ tối nhiệt độ từ 2-8 độ C, tránh ánh sáng. Sau 8 giờ phải hủy bỏ vắc xin theo quy định.",
                                           "Vắc xin MMR-II cho trẻ từ 12 tháng tuổi – < 7 tuổi (chưa tiêm Sởi đơn hay MMR II) có lịch tiêm 2 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: 3 tháng sau mũi 1 (ưu tiên) hoặc hẹn mũi 2 lúc 4-6 tuổi.;Vắc xin MMR-II cho trẻ từ 7 tuổi và người lớn có lịch tiêm 2 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: 1 tháng sau mũi 1.",
                                           "Vắc xin MMR-II phòng Sởi – Quai bị – Rubella được chỉ định dành cho trẻ từ 12 tháng tuổi trở lên và người  lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Tetraxim (Pháp)",
                                           "vac-xin-tetraxim.jpg",
                                           new HashSet<>(List.of("4-6 tuổi")),
                                           "Vắc xin 4 trong 1 Tetraxim (Pháp) được chỉ định để phòng ngừa các bệnh ho gà, bạch hầu, uốn ván, bại liệt cho trẻ từ 2 tháng tuổi trở lên đến 13 tuổi tùy theo mỗi quốc gia.",
                                           "Sanofi Pasteur (Pháp)",
                                           "Tiêm bắp ở trẻ nhũ nhi và tiêm vùng cơ delta ở trẻ 2 tháng tuổi đến 13 tuổi.",
                                           "Trẻ bị dị ứng với một trong các thành phần của vắc xin.;Nếu trẻ bị bệnh não tiến triển (thương tổn ở não).;Nếu trẻ từng bị bệnh não (tổn thương ở não) trong vòng 7 ngày sau khi tiêm liều vắc xin ho gà (ho gà vô bào hay toàn tế bào).;Nếu trẻ bị sốt hay bị bệnh cấp tính (phải hoãn việc tiêm ngừa lại).",
                                           "Tại chỗ tiêm: đỏ, sưng (có thể hơn 5cm) hoặc lan ra toàn bộ chi bên tiêm. Xảy ra trong vòng 24 – 72 giờ sau khi tiêm vắc xin và tự khỏi trong vòng 3-5 ngày.;Toàn thân: sốt, tiêu chảy, kém ăn, quấy khóc.",
                                           5, 5,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C. Không được để đông băng.",
                                           "Lịch tiêm 5 mũi, như sau:;– Mũi 1: lần tiêm đầu tiên;– Mũi 2: 1 tháng sau mũi 1;– Mũi 3: 1 tháng sau mũi 2;– Mũi 4: 1 năm sau mũi 3;– Mũi 5: 3 năm sau mũi 4 (trẻ 4 – 6 tuổi)",
                                           "Vắc xin 4 trong 1 Tetraxim (Pháp) được chỉ định tiêm cho trẻ từ 2 tháng tuổi trở lên đến 13 tuổi (tùy theo khuyến cáo chính thức của từng quốc gia)."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Adacel (Canada)",
                                           "Adacel.jpg",
                                           new HashSet<>(Arrays.asList("9-18 tuổi", "Phụ nữ trước mang thai",
                                                                       "Người trưởng thành")),
                                           "Vắc xin Adacel tạo miễn dịch chủ động nhắc lại nhằm phòng bệnh ho gà – bạch hầu – uốn ván.",
                                           "Nghiên cứu và phát triển bởi Sanofi Pasteur – Pháp. Vắc xin Adacel được sản xuất tại Canada.",
                                           "Tiêm bắp. ",
                                           "Người quá mẫn với thành phần của vắc xin.;Bệnh lý não (ví dụ hôn mê, giảm tri giác, co giật kéo dài) xảy ra trong vòng 7 ngày sau khi tiêm một liều vắc xin bất kỳ có chứa thành phần ho gà mà không xác định được nguyên nhân nào khác.",
                                           "Tại chỗ tiêm: đau, sưng, đỏ;Toàn thân: sốt, mệt mỏi, đau đầu, rối loạn tiêu hóa",
                                           1, 1,
                                           "Vắc xin được bảo quản ở nhiệt độ từ +2 đến +8 độ C. Không được để đông băng.",
                                           "Trẻ từ 4 tuổi trở lên và người lớn đã tiêm lịch cơ bản bạch hầu – ho gà – uốn ván:;Tiêm nhắc 1 mũi mỗi 10 năm hoặc tiêm nhắc theo độ tuổi khuyến cáo;Trẻ từ 4 tuổi trở lên và người lớn chưa tiêm đủ lịch cơ bản bạch hầu – ho gà – uốn ván:;Mũi 1: Lần tiêm đầu tiên; Mũi 2: Một tháng sau mũi 1; Mũi 3: Sáu tháng sau mũi 2;Tiêm nhắc một mũi mỗi 10 năm hoặc tiêm nhắc theo độ tuổi khuyến cáo.",
                                           "Vắc xin Adacel được chỉ định cho trẻ từ 4 tuổi trở lên và người lớn đến 64 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Imojev (Thái Lan)",
                                           "vac-xin-imojev.jpg",
                                           new HashSet<>(Arrays.asList("7-12 tháng", "4-6 tuổi")),
                                           "Imojev là vắc xin phòng viêm não Nhật Bản được chỉ định cho trẻ em từ 9 tháng tuổi trở lên và người lớn.",
                                           "Nghiên cứu và phát triển Sanofi Pasteur (Pháp). Sản xuất tại Thái Lan.",
                                           "Trẻ từ 9 tháng tuổi đến 24 tháng tuổi: Tiêm tại mặt trước – bên của đùi hoặc vùng cơ Delta ở cánh tay.;Trẻ từ 2 tuổi trở lên và người lớn: Tiêm tại vùng cơ Delta ở cánh tay.;Liều tiêm: 0,5ml/liều Imojev hoàn nguyên.",
                                           "Người có tiền sử phản ứng dị ứng với bất kỳ thành phần nào của Imojev.;Người suy giảm miễn dịch bẩm sinh hoặc mắc phải làm suy yếu miễn dịch tế bào.;Người nhiễm HIV có triệu chứng hoặc bằng chứng suy giảm chức năng miễn dịch.;Phụ nữ có thai hoặc cho con bú sữa mẹ.",
                                           "Phản ứng tại chỗ tiêm: Đỏ, ngứa, sưng, đau.;Phản ứng toàn thân: Mệt mỏi, khó chịu, đau đầu, đau cơ, ở trẻ em có thể sốt còn người lớn có thể phát ban.",
                                           2, 1,
                                           "Vắc xin Imojev cần bảo quản ở nhiệt độ từ 2 đến 8 độ C, không được để đông băng. Giữ vắc xin trong hộp để tránh ánh sáng.",
                                           "Trẻ từ 9 tháng tuổi đến dưới 18 tuổi (chưa tiêm vắc xin Jevax lần nào) có lịch tiêm 2 mũi: ;Mũi 1: Là mũi tiêm đầu tiên.;Mũi 2: Cách 1 năm sau mũi đầu tiên.;Người tròn 18 tuổi trở lên: Tiêm 1 mũi duy nhất.",
                                           "Vắc xin Imojev được chỉ định phòng viêm não Nhật Bản cho trẻ em từ 9 tháng tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Avaxim (Pháp)",
                                           "vac-xin-avaxim-80U.jpg",
                                           new HashSet<>(List.of("7-12 tháng")),
                                           "Vắc xin Avaxim 80U tạo miễn dịch chủ động phòng ngừa virus gây bệnh viêm gan siêu vi A.",
                                           "Sanofi Pasteur (Pháp)",
                                           "Tiêm bắp vào cơ delta trên cánh tay.",
                                           "Hoãn việc tiêm chủng nếu có sốt hay nhiễm trùng cấp tính. Bệnh mãn tính trong giai đoạn tiến triển.;Không tiêm cho người bị dị ứng với hoạt chất, với bất kỳ thành phần tá dược nào có trong vắc xin, với neomycin, với polysorbate hoặc nếu trước đây đã từng bị mẩn mãn sau khi tiêm vắc xin này.",
                                           "Phản ứng tại chỗ tiêm: Đau tại vết tiêm, đôi khi có quầng đỏ, sưng, nốt cứng.;Phản ứng toàn thân: Sốt nhẹ, đau đầu, đau cơ, mệt mỏi, rối loạn tiêu hóa, phát ban…",
                                           2, 0,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C. Tránh ánh sáng. Không được đông băng.",
                                           "Mũi 1: lần tiêm đầu tiên;Mũi 2: 6 tháng sau mũi 1",
                                           "Vắc xin Avaxim 80U được chỉ định cho trẻ từ 12 tháng đến dưới 16 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Havax (Việt Nam)",
                                           "vac-xin-havax.jpg",
                                           new HashSet<>(List.of("7-12 tháng")),
                                           "Vắc xin Havax được dùng để phòng ngừa bệnh viêm gan A cho mọi đối tượng người lớn và trẻ em từ 24 tháng tuổi trở lên, đặc biệt cho những người có nguy cơ phơi nhiễm với virus viêm gan A.",
                                           "Vabiotech – Việt Nam",
                                           "Tiêm bắp. Không được tiêm vào đường tĩnh mạch hoặc trong da.;Ở người lớn thì tiêm vắc-xin vào vùng cơ Delta song ở trẻ em nên tiêm vào vùng đùi ngoài thì tốt hơn vì cơ Delta còn nhỏ.",
                                           "Không tiêm Havax cho những người quá nhạy cảm với bất cứ thành phần nào của vắc xin.;Bệnh tim, bệnh thận hoặc bệnh gan.;Bệnh tiểu đường.;Bệnh ung thư máu và các bệnh ác tính nói chung.;Bệnh quá mẫn.;Không được tiêm tĩnh mạch trong bất cứ trường hợp nào.;Không tiêm cho trẻ em dưới 2 tuổi (24 tháng tuổi).;Không tiêm cho các đối tượng mắc bệnh bẩm sinh.;Hoãn tiêm ở người đang mệt mỏi, sốt cao hoặc phản ứng toàn thân hoặc bệnh nhiễm trùng đang tiến triển.",
                                           "Tại chỗ tiêm: có thể sưng quầng đỏ tại chỗ tiêm 1-2 ngày.;Toàn thân: Sốt nhẹ, đau đầu, đau cơ, mệt mỏi,…",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ +2 đến +8 độ C. Không để đông đá. Tránh ánh sáng.",
                                           "Mũi 1: lần tiêm đầu tiên.;Mũi 2: 6 tháng sau mũi 1.",
                                           "Vắc xin Havax được khuyến cáo chủng ngừa cho trẻ từ 24 tháng tuổi đến dưới 18 tuổi, sử dụng cho những người có nguy cơ phơi nhiễm với virus viêm gan A"));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Twinrix (Bỉ)",
                                           "vac-xin-twinrix.jpg",
                                           new HashSet<>(Arrays.asList("7-12 tháng", "4-6 tuổi")),
                                           "Vắc xin Twinrix được chỉ định để phòng 2 bệnh viêm gan A và viêm gan B ở trẻ em từ 1 tuổi và người lớn chưa có miễn dịch.",
                                           "Glaxosmithkline (GSK) – Bỉ",
                                           "Tiêm bắp.",
                                           "Người nhạy cảm với bất kỳ thành phần nào trong vắc xin hoặc có biểu hiện quá mẫn với vắc xin phòng viêm gan B và viêm gan A đơn lẻ.",
                                           "Phản ứng tại chỗ tiêm: sưng, đau, đỏ da.;Phản ứng toàn thân: mệt mỏi, đau đầu, có thể sốt.",
                                           2, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C và không được đông băng.",
                                           "Lịch tiêm 2 mũi cho trẻ từ 12 tháng – dưới 16 tuổi:;– Mũi 1: lần tiêm đầu tiên.;– Mũi 2: 6 tháng sau mũi 1.;Lịch tiêm 3 mũi cho người từ 16 tuổi trở lên:;– Mũi 1: lần tiêm đầu tiên;– Mũi 2: 1 tháng sau mũi 1.;– Mũi 3: 6 tháng sau mũi 1.",
                                           "Vắc xin phòng bệnh Viêm gan A+B (Twinrix) được chỉ định tiêm cho trẻ từ 12 tháng tuổi trở lên và người lớn. "));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin uốn ván hấp phụ (Việt Nam)",
                                           "vac-xin-uon-van-hap-phu-tt.jpg",
                                           new HashSet<>(Arrays.asList("Phụ nữ trước mang thai", "Người trưởng thành")),
                                           "Vắc xin uốn ván hấp phụ (TT) giúp tạo miễn dịch chủ động phòng bệnh uốn ván cho người lớn và trẻ em. ",
                                           "Viện vắc xin và sinh phẩm y tế Nha Trang IVAC – Việt Nam",
                                           "Vắc xin uốn ván hấp phụ (TT) được chỉ định tiêm bắp sâu, liều tiêm 0,5ml.;Không tiêm tĩnh mạch trong bất cứ trường hợp nào.;Lắc tan đều trước khi tiêm.",
                                           "Không tiêm cho người dị ứng, quá mẫn với bất kỳ thành phần nào của vắc xin.;Không tiêm cho đối tượng có các biểu hiện dị ứng ở lần tiêm vắc xin trước.;Không dùng cho người có các dấu hiệu, triệu chứng thần kinh sau khi tiêm các liều trước đó.;Hoãn tiêm với các trường hợp sốt cao hoặc đang mắc các bệnh cấp tính.",
                                           "Tại chỗ tiêm: đau, quầng đỏ, nốt cứng hay sưng xuất hiện trong vòng 48 giờ sau khi tiêm và kéo dài trong 1-2 ngày. Tuy nhiên các triệu chứng này thường nhẹ và tự mất đi.;Toàn thân: sốt, đau đầu, đổ mồ hôi, ớn lạnh, đau cơ, đau khớp.",
                                           3, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C. Không được để đông đá vắc xin. Loại bỏ vắc xin nếu bị đông đá.",
                                           "Lịch tiêm cơ bản 3 mũi;Mũi 1: lần tiêm đầu tiên; Mũi 2: 1 tháng sau mũi 1; Mũi 3: 6 tháng sau mũi 2; Lịch tiêm ở phụ nữ mang thai:; Lần mang thai đầu tiên: tiêm 2 mũi;Mũi 1: tiêm sớm khi phát hiện có thai – thường tiêm vào 3 tháng giữa thai kỳ;Mũi 2: cách mũi 1 ít nhất một tháng, yêu cầu trước ngày dự sinh ít nhất 1 tháng;Lần mang thai thứ 2, 3, 4: tiêm 01 mũi – yêu cầu trước ngày dự sinh ít nhất 1 tháng.; Lịch tiêm Khách hàng đang bị phơi nhiễm (Cần khai thác trước đó KH có tiêm mũi Uốn ván nào chưa);Nếu đã tiêm đủ 5 mũi: Không cần tiêm thêm;Nếu đã tiêm từ 3 mũi trở lên: Tiêm thêm vắc xin uốn ván hấp phụ (TT), lưu ý không tiêm huyết thanh (SAT).;Nếu đã tiêm < 3 mũi: Tiêm tiếp cho đủ 5 mũi.",
                                           "Chỉ định phòng bệnh Uốn ván cho người lớn và trẻ em"));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Typhim VI (Pháp)",
                                           "vac-xin-typhim-vi.jpg",
                                           new HashSet<>(List.of("13-24 tháng")),
                                           "Vắc xin Typhim VI phòng ngừa bệnh Thương hàn (bệnh về đường tiêu hóa) gây ra bởi vi khuẩn thương hàn (Salmonella typhi) cho trẻ từ trên 2 tuổi và người lớn. ",
                                           "Sanofi Pasteur (Pháp)",
                                           "Tiêm bắp hoặc tiêm dưới da.",
                                           "Mẫn cảm với các thành phần trong vắc xin.;Không tiêm bắp cho người bị rối loạn đông máu hoặc giảm tiểu cầu.;Trẻ em dưới 2 tuổi vì đáp ứng miễn dịch thấp.",
                                           "Phản ứng tại chỗ: sưng, đau, có quầng đỏ tại vết tiêm.;Phản ứng toàn thân: sốt, mệt mỏi, đau đầu, đau cơ, buồn nôn và đau bụng.",
                                           1, 1,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C",
                                           "Lịch tiêm 01 mũi.;Tiêm nhắc: mỗi 3 năm nếu có nguy cơ nhiễm bệnh.",
                                           "Vắc xin thương hàn được chỉ định cho trẻ từ trên 2 tuổi (2 tuổi 1 ngày) trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin mOrcvax (Việt Nam)",
                                           "vac-xin-mORCVAX.jpg",
                                           new HashSet<>(Arrays.asList("13-24 tháng", "4-6 tuổi")),
                                           "Vắc xin mORCVAX phòng bệnh bệnh truyền nhiễm cấp tính do vi khuẩn tả Vibrio cholerae gây nên.",
                                           "Vabiotech – Việt Nam",
                                           "Chỉ dùng đường uống. Liều dùng: 1,5ml/liều.",
                                           "Không dùng vắc xin mORCVAX cho trẻ đã quá mẫn cảm ở lần uống đầu tiên hoặc với bất kỳ thành phần nào của vắc xin.;Không dùng cho người mắc các bệnh nhiễm trùng đường ruột cấp tính, các bệnh cấp tính và mãn tính đang thời kỳ tiến triển.;Không dùng cho bệnh nhân đang sử dụng thuốc ức chế miễn dịch, thuốc chống ung thư. ",
                                           "Thường gặp: Sau khi uống vắc xin có thể có cảm giác buồn nôn hoặc nôn ói…;Hiếm gặp: Đau đầu, đau bụng, tiêu chảy, sốt, tuy nhiên những triệu chứng này sẽ tự khỏi mà không cần điều trị.",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C.",
                                           "Lịch uống cơ bản: 2 liều uống cách nhau tối thiểu 2 tuần (14 ngày).;Lịch nhắc lại: Uống lặp lại sau lịch uống cơ bản 2 năm hoặc trước mỗi mùa dịch tả. Phác đồ uống 2 liều, khoảng cách giữa 2 liều tối thiểu là 2 tuần.",
                                           "Vắc xin tả mORCVAX được chỉ định cho trẻ từ 2 tuổi trở lên và người lớn. "));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Quimihib (Cu ba)",
                                           "vac-xin-quimi-hib.jpg",
                                           new HashSet<>(List.of("Ngoài các nhóm trên")),
                                           "Vắc xin Quimihib phòng ngừa bệnh viêm phổi, viêm màng não mủ do tác nhân Haemophilus Influenzae type b gây ra ở trẻ nhỏ.",
                                           "Center for Genetic Engineering and Biotechnology (C.I.G.B) – Cuba",
                                           "Tiêm bắp.;Tiêm vào vùng trước bên đùi của trẻ dưới 2 tuổi.;Tiêm vào vùng cơ delta đối với trẻ trên 2 tuổi.",
                                           "Không tiêm QuimiHib cho đối tượng dị ứng, mẫn cảm với bất kỳ thành phần nào của vắc xin.;Không tiêm cho đối tượng đang sốt cao hoặc bệnh cấp tính.;Không tiêm vào mạch máu, tĩnh mạch trong bất cứ trường hợp nào.",
                                           "Tại chỗ tiêm: đỏ, sưng, đau và có thể ngứa tại vị trí tiêm;Toàn thân: Sốt nhẹ, quấy khóc, biếng ăn, buồn nôn, nôn , tiêu chảy",
                                           3, 0,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C. Không được để đông đá vắc xin. Loại bỏ vắc xin nếu bị đông đá. Vắc xin cần được tiêm ngay sau khi mở lọ.",
                                           "Lịch tiêm cho trẻ dưới hoặc bằng 12 tháng tuổi:;Tiêm 3 mũi cơ bản khi trẻ được 2 – 4 – 6 tháng tuổi (phải đảm bảo mũi sau cách mũi trước tối thiểu 8 tuần).;Mũi nhắc thứ 4 tiêm lúc trẻ từ 15 – 18 tháng tuổi (mũi 4 phải cách mũi 3 tối thiểu 2 tháng).;Lịch tiêm cho trẻ trên 12 tháng tuổi: Chỉ cần tiêm duy nhất 1 mũi QuimiHib.",
                                           "Vắc xin QuimiHib dành cho trẻ em từ 2 tháng tuổi đến 15 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest(
                        "Vắc xin Uốn ván, bạch hầu hấp phụ (Việt Nam)",
                        "vac-xin-uon-van-bach-hau-hap-phu-td.jpg",
                        new HashSet<>(List.of("Ngoài các nhóm trên")),
                        "Vắc xin uốn ván – bạch hầu hấp phụ (Td) được chỉ định cho trẻ em lứa tuổi lớn (từ 7 tuổi trở lên) và người lớn nhằm gây miễn dịch, phòng các bệnh uốn ván và bạch hầu.",
                        "Viện vắc xin và sinh phẩm y tế Nha Trang IVAC (Việt Nam)",
                        "Vắc xin uốn ván – bạch hầu hấp phụ (Td) được chỉ định tiêm bắp sâu. Không được tiêm dưới da hoặc tĩnh mạch.;Lắc tan đều trước khi dùng.;Liều tiêm 0,5 ml.",
                        "Tiền sử quá mẫn với thành phần của vắc xin. Có biểu hiện dị ứng với kháng nguyên bạch hầu và uốn ván ở những lần tiêm trước.;Tạm hoãn tiêm vắc xin uốn ván – bạch hầu hấp phụ (Td) trong những trường hợp có bệnh nhiễm trùng cấp tính, sốt chưa rõ nguyên nhân.;Không tiêm bắp cho người có rối loạn chảy máu như Hemophilia hoặc giảm tiểu cầu.",
                        "Tại chỗ tiêm: đau, sưng, quầng đỏ.;Toàn thân: sốt nhẹ, mệt mỏi, khó chịu, đau đầu.",
                        3, 1,
                        "Vắc xin nên được bảo quản ở nhiệt độ từ 2 đến 8 độ C, và không được để đông băng.Để xa tầm tay trẻ em.",
                        "Trẻ em từ tròn 7 tuổi trở lên đến dưới 10 tuổi chưa từng tiêm vắc xin có chứa thành phần Bạch hầu – Ho gà – Uốn ván hoặc chưa rõ tình trạng đã tiêm ngừa hay chưa có lịch tiêm 3 mũi như sau:;Mũi 1: lần tiêm đầu tiên.;Mùi 2: cách mũi 1 tối thiểu 1 tháng;Mũi 3: cách mũi 2 tối thiểu 6 tháng;Mũi 4: 5 năm sau mũi 3;Sau đó, các mỗi 10 năm tiêm 1 mũi nhắc;Trẻ từ 10 tuổi trở lên chưa từng tiêm vắc xin Bạch hầu – Ho gà – Uốn ván hoặc không rõ tình trạng tiêm ngừa:;Mũi 1: lần đầu tiên;Mũi 2: Cách mũi 1 tối thiểu 1 tháng;Mũi 3: Cách mũi 2 tối thiểu 6 tháng;Sau đó, các mũi nhắc : 01 mũi nhắc mỗi 10 năm;Trẻ em từ tròn 7 tuổi trở lên đến dưới 10 tuổi đã tiêm đủ 4 mũi cơ bản vắc xin 6 trong 1 / 5 trong 1 / 4 trong 1 ( trước 4 tuổi) có lịch tiêm 1 mũi như sau:;Mũi 1: lần tiêm đầu tiên (cách mũi thứ 4 ít nhất 3 năm);Liều tiêm nhắc: tiêm 1 mũi sau 05 năm;Sau đó, các mũi nhắc 01 mũi nhắc mỗi 10 năm;Người từ 10 tuổi trở lên đã tiêm đủ 4 mũi vắc xin 6 trong 1 / 5 trong 1 / 4 trong 1 ( trước 10 tuổi):;Tiêm 1 mũi ( cách mũi thứ 4 ít nhất 1 năm);Sau đó, các mũi nhắc: 01 mũi nhắc mỗi 10 năm",
                        "Được chỉ định chủng ngừa cho người từ 7 tuổi trở lên tạo đáp ứng kháng thể chống 2 bệnh bạch hầu và uốn ván.Không chống chỉ định cho phụ nữ có thai và đang cho con bú. Cân nhắc sử dụng theo chỉ định của bác sĩ."));

        vaccineCreationRequestList.add(new VaccineCreationRequest(
                "Huyết thanh uốn ván (Việt Nam)",
                "huyet-thanh-uon-van-SAT.jpg",
                new HashSet<>(List.of("Ngoài các nhóm trên")),
                "Huyết thanh uốn ván SAT được dùng để phòng ngừa uốn ván ở người vừa mới bị vết thương có thể nhiễm bào tử uốn ván, bao gồm những người không tiêm ngừa uốn ván trong 10 năm gần đây, hoặc không nhớ rõ lịch tiêm uốn ván. ",
                "Viện Vắc xin và Sinh phẩm Y tế (IVAC – Việt Nam)",
                "Huyết thanh uốn ván SAT được chỉ định tiêm bắp.",
                "Những trường hợp có tiền sử dị ứng với huyết thanh kháng độc tố uốn ván nguồn gốc từ ngựa. Những trường hợp này nếu bắt buộc dùng nên dùng loại huyết thanh uốn ván nguồn gốc từ người.;Phụ nữ đang mang thai.",
                "Huyết thanh Uốn ván SAT có độ an toàn cao. Tuy nhiên, những người có cơ địa dị ứng, người dùng huyết thanh nhiều lần thường có nguy cơ phản ứng dị ứng với huyết thanh như nổi mề đay, ngứa phù, viêm thận, trường hợp nặng có thể bị choáng, sốc phản vệ.",
                1, 1,
                "Bảo quản ở nhiệt độ lạnh (từ 2 – 8 độ C). Không được đóng băng.",
                "Dự phòng sau khi bị thương:;Nhất thiết phải dùng phương pháp Besredka: Tiêm 0.1ml, chờ nửa giờ, tiêm 0.25ml, chờ nửa giờ, nếu không phản ứng, tiêm hết liều còn lại. Liều thông thường huyết thanh kháng độc tố uốn ván ở người lớn và trẻ em để dự phòng sau khi bị thương là 1500 đvqt, tiêm càng sớm càng tốt sau khi bị thương. Tăng liều gấp đôi đối với vết thương dễ gây uốn ván hoặc chậm trễ khi bắt đầu tiêm phòng hoặc ở người có thể trọng quá cao.;Điều trị uốn ván:;Mặc dù liều điều trị tối ưu và liều có hiệu quả trong điều trị bệnh uốn ván còn chưa được xác định, liều khuyên dùng cho người lớn và trẻ em là 3000 – 6000 đơn vị.;Uốn ván sơ sinh: 5000 – 10.000 đvqt.;Trẻ em và người lớn: 50.000 – 100.000 đvqt, tiêm dưới da 1/2 liều và nửa còn lại tiêm bắp.",
                "Huyết thanh Uốn ván SAT được dùng để dự phòng bệnh uốn ván trong trường hợp bị các vết thương, vết cắn súc vật. Điều trị bệnh nhân bị bệnh uốn ván (khi đã có triệu chứng bệnh)."));

        vaccineCreationRequestList.add(new VaccineCreationRequest(
                "Vắc xin phòng COVID-19 của AstraZeneca ",
                "vacxin-covid-19.jpg",
                new HashSet<>(List.of("Ngoài các nhóm trên")),
                "COVID-19 vaccine Astrazeneca là một loại vắc xin được sử dụng để bảo vệ các đối tượng từ 18 tuổi trở lên chống lại COVID-19. Vắc xin giúp cho hệ miễn dịch của người được được tiêm chủng có khả năng nhận biết và tiêu diệt virus corona (SARS-COV-2).",
                "AstraZeneca (Vương quốc Anh)",
                "Tiêm bắp.",
                "Tiền sử phản vệ với vắc xin phòng COVID-19 cùng loại (lần trước);;Có bất cứ chống chỉ định nào theo công bố của nhà sản xuất.",
                "Phản ứng tại chỗ tiêm: Nổi ban đỏ, chai cứng, sưng, ngứa, đỏ tại chỗ tiêm.;Một số phản ứng toàn thân khác: Sốt, mệt mỏi, ớn lạnh, đau đầu, đau cơ hoặc đau khớp, buồn ngủ, giảm cảm giác thèm ăn, nôn mửa, tiêu chảy.",
                2, 2,
                "Bảo quản ở mức nhiệt 2 – 8 độ C, trong điều kiện bảo quản lạnh thông thường với thời gian trong vòng 6 tháng.",
                "Tại Việt Nam, vắc xin phòng COVID-19 của AstraZeneca được chỉ định tiêm phòng cho những người từ 18 tuổi trở lên.;Lịch tiêm gồm 2 mũi:;Mũi 1: Lần đầu tiên tiêm.;Mũi 2: Sau mũi đầu tiên từ 4 – 12 tuần.",
                "Vắc xin phòng COVID-19 của AstraZeneca được chỉ định tiêm cho người lớn từ 18 tuổi trở lên để tạo miễn dịch chủ động phòng bệnh viêm đường hô hấp cấp do virus SARS-CoV-2 gây ra"));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin GCFLU Quadrivalent",
                                           "GCFlu-Quadrivalent.jpg",
                                           new HashSet<>(Arrays.asList("2-6 tháng", "4-6 tuổi", "9-18 tuổi")),
                                           "Vắc xin GCFlu Quadrivalent được chỉ định để phòng ngừa bệnh cúm mùa do virus cúm thuộc 2 chủng cúm A (H1N1, H3N2) và 2 chủng cúm B (Victoria và Yamagata).",
                                           "Green Cross (Hàn Quốc)",
                                           "Ở trẻ 6-35 tháng: vị trí thích hợp để tiêm bắp là mặt trước bên của đùi (hoặc cơ delta của vùng trên cánh tay nếu khối lượng cơ bắp đủ). Ở trẻ em từ 36 tháng tuổi và người lớn: cơ delta của vùng trên cánh tay.",
                                           "Bệnh nhân sốt hoặc người bị suy dinh dưỡng.;Bệnh nhân bị rối loạn tim mạch, rối loạn thận hoặc bệnh gan trong khi bệnh đang trong giai đoạn cấp tính, hoặc hoạt động.;Bệnh nhân mắc bệnh hô hấp cấp tính hoặc bệnh truyền nhiễm tích cực khác.;Bệnh nhân mắc bệnh thể ẩn và trong giai đoạn dưỡng bệnh.;Người quá mẫn với các thành phần của vắc xin.;Người bị dị ứng với trứng, thịt gà, mọi sản phẩm từ thịt gà.;Người bị sốt trong vòng 2 ngày hoặc có triệu chứng dị ứng như phát ban toàn thân sau tiêm tại lần tiêm phòng trước.;Người có triệu chứng co giật trong vòng 1 năm trước khi tiêm chủng.;Người có hội chứng Guillain-Barre hoặc người bị rối loạn thần kinh trong vòng 6 tuần kể từ lần chủng ngừa cúm trước.;Người được chẩn đoán mắc bệnh suy giảm miễn dịch.",
                                           "Các phản ứng tại chỗ: đỏ, đau, sưng, cứng vị trí tiêm;Các phản ứng toàn thân: sốt, khó chịu, đầu đầu, đổ mồ hôi, chứng đau cơ, đau khớp",
                                           2, 1,
                                           "Vắc xin cúm GC Flu Quadrivalent phải được bảo quản tại 2°C đến 8°C (trong tủ lạnh). Không được đông băng. Bảo quản trong bao bì gốc. Tránh tiếp xúc ánh sáng.",
                                           "Trẻ từ 6 tháng đến 9 tuổi, có lích tiêm 2 mũi: ;Mũi 1: lần tiêm đầu tiên;Mũi 2: một tháng sau mũi 1;Trẻ từ 9 tuổi trở lên và người lớn: Lịch tiêm 01 mũi duy nhất và nhắc lại hằng năm.",
                                           "GC FLU Quadrivalent (Hàn Quốc) dành cho trẻ từ 6 tháng tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Varivax (Mỹ)",
                                           "vac-xin-varivax.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("7-12 tháng", "9-18 tuổi", "Phụ nữ trước mang thai",
                                                                 "Người trưởng thành")),
                                           "Vắc xin Varivax tạo miễn dịch chủ động phòng bệnh Thủy đậu do virus Varicella Zoster gây ra",
                                           " Merck Sharp and Dohm (Mỹ)",
                                           "Vắc xin Varivax được chỉ định tiêm dưới da. Liều đơn 0.5ml",
                                           "Mẫn cảm với bất kỳ thành phần nào của vắc xin, bao gồm cả: gelatin, neomycin.;Người đang mắc các bệnh bạch, loạn sản máu, các bệnh u lympho, hoặc các khối u ác tính ảnh hưởng đến hệ bạch huyết, tủy xương.;đang điều trị bằng các thuốc ức chế miễn dịch (bao gồm corticoid liều cao). Hoặc đang mắc bệnh suy giảm miễn dịch mắc phải (AIDS).;Người đã có miễn dịch do mắc phải.;Người đang mắc các bệnh lý tiến triển, sốt cao trên 38 độ C. Tuy nhiên không có chống chỉ định cho trường hợp sốt nhẹ.;Người mắc bệnh lao thể hoạt động chưa được điều trị.",
                                           "Tại chỗ tiêm: phát ban dạng thủy đậu, đau, đỏ, sưng.;Toàn thân: sốt",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C.",
                                           "Trẻ từ 12 tháng đến 12 tuổi có lịch tiêm 2 mũi:;– Mũi 1: lần tiêm đầu tiên.;– Mũi 2: 3 tháng sau mũi 1 (ưu tiên) hoặc hẹn mũi 2 lúc 4-6 tuổi.; Trẻ từ 13 tuổi trở lên và người lớn có lịch tiêm 02 mũi:;– Mũi 1: lần tiêm đầu tiên.;– Mũi 2: cách mũi 1 ít nhất 01 tháng.",
                                           "Vắc xin Varivax là vắc xin dạng đông khô của virus thủy đậu (varicella) sống giảm độc lực, được chỉ định phòng thủy đậu cho các đối tượng từ 12 tháng tuổi trở lên."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Varicella (Hàn Quốc)",
                                           "Varicella.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("7-12 tháng", "9-18 tuổi", "Phụ nữ trước mang thai",
                                                                 "Người trưởng thành")),
                                           "Vắc xin Varicella tạo miễn dịch dịch chủ động phòng bệnh thủy đậu do virus Varicella Zoster gây ra.",
                                           "Green Cross – Hàn Quốc",
                                           "Vắc xin phải được sử dụng ngay không quá 30 phút sau khi hoàn nguyên với nước hồi chỉnh cung cấp.;Tiêm dưới da. Liều đơn 0.5ml",
                                           "Quá mẫn với bất kỳ thành phần nào của vắc xin.;Không dùng vắc xin cho đối tượng đang sốt hoặc suy dinh dưỡng.;Bệnh tim mạch, rối loạn chức năng gan, thận.;Có tiền sử quá mẫn với kanamycin và Erythromycin.;Có tiền sử co giật trong vòng 1 năm trước khi tiêm vắc xin.;Suy giảm miễn dịch tế bào.;Có thai hoặc 2 tháng trước khi định có thai.;Đã tiêm phòng vắc xin sống khác trong vòng 1 tháng gần đây (Sởi, quai bị, rubella, lao, bại liệt).;Suy giảm miễn dịch tiên phát hoặc mắc phải như AIDS hoặc các biểu hiện lâm sàng của nhiễm virus gây suy giảm miễn dịch ở người.;Trẻ em dưới 12 tháng tuổi.;Bệnh nhân mắc bệnh bạch cầu tủy cấp, bệnh bạch cầu tế bào lympho T hoặc u lympho ác tính.;Bệnh nhân bị ức chế mạnh hệ thống miễn dịch do xạ trị hoặc giai đoạn tấn công trong điều trị bệnh bạch cầu.",
                                           "Tại chỗ tiêm: phát ban dạng thủy đậu, đau, đỏ, sưng.;Toàn thân: sốt",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C. Tránh ánh sáng trực tiếp.",
                                           "Trẻ từ 12 tháng đến 12 tuổi có lịch tiêm 2 mũi:;– Mũi 1: lần tiêm đầu tiên.;– Mũi 2: 3 tháng sau mũi 1 (ưu tiên) hoặc hẹn mũi 2 lúc 4-6 tuổi.;Trẻ từ từ 13 tuổi trở lên và người lớn có lịch tiêm 2 mũi:;– Mũi 1: lần tiêm đầu tiên.;– Mũi 2: cách mũi 1 ít nhất 01 tháng",
                                           "Vắc xin Varicella là vắc xin dạng đông khô của virus thủy đậu (varicella) sống giảm độc lực, được chỉ định phòng thủy đậu cho trẻ từ 12 tháng tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest(
                        "Vắc xin Measles – Mumps – Rubella (Ấn Độ)",
                        "MMR.jpg",
                        new HashSet<>(Arrays.asList("13-24 tháng", "4-6 tuổi", "9-18 tuổi")),
                        "Vắc xin MMR là vắc xin sống, giảm độc lực, được đông khô và có nước hồi chỉnh kèm theo. Sản phẩm có dạng viên đông khô màu trắng ánh vàng. Vắc xin đạt được các tiêu chuẩn của W.H.O khi kiểm tra bằng các phương pháp theo hướng dẫn trong tạp chí W.H.O TRS 840 (1994).",
                        "Serum Institute of India Ltd",
                        "Vắc xin được tiêm theo đường tiêm dưới da sâu ở vị trí mặt trước bên đùi đối với trẻ nhỏ và vị trí bắp tay đối với trẻ lớn hơn.",
                        "Những người đang sử dụng corticosteroids, các thuốc ức chế miễn dịch khác hoặc đang xạ trị có thể không có đáp ứng miễn dịch tối ưu.;Không được tiêm vắc xin cho những người đang sốt, có thai, mắc các bệnh truyền nhiễm cấp tính, bệnh bạch hầu, thiếu máu nghiêm trọng và các bệnh nặng khác về máu, có tổn thương chức năng thận, bệnh tim mất bù, đang sử dụng gammaglobulin hoặc truyền máu hoặc các đối tượng có khả năng dị ứng với các thành phần của vắc xin.;Vắc xin có thể còn vết của neomycin.;Chống chỉ định tuyệt đối với người có phản ứng quá mẫn hoặc dạng quá mẫn với neomycin, có tiền sử phản ứng quá mẫn hoặc dạng quá mẫn.;Không chống chỉ định với các trường hợp sốt nhẹ, viêm đường hô hấp nhẹ hoặc tiêu chảy và các triệu chứng ốm nhẹ khác.;Không có báo cáo phản ứng phụ nghiêm trọng nào ở phụ nữ có thai vô tình được tiêm vắc xin có thành phần Rubella ở giai đoạn sớm thai kỳ.",
                        "Sốt nhẹ, ngứa, nổi hạch bạch huyết, đau cơ và cảm giác khó chịu được báo cáo là phổ biến.;Ở một số trường hợp rất hiếm gặp, ở người mẫn cảm, vắc xin có thể gây dị ứng nổi mề đay, ngứa và phát ban trong vòng 24h sau tiêm.",
                        2, 0,
                        "Cả vắc xin và nước hồi chỉnh đều phải tránh ánh sáng. Cần bảo quản vắc xin ở chỗ tối, nhiệt độ từ 2-8 độ C. Nước hồi chỉnh phải bảo quản nơi mát, không để đông băng.",
                        "Ủy ban tư vấn về thực hành tiêm chủng (ACIP) khuyến cáo nên tiêm liều MMR đầu tiên khi trẻ 12-15 tháng tuổi và tiêm MMR liều thứ hai khi trẻ được 4-6 tuổi ( vì nguy cơ tái nhiễm tăng cao khi trẻ bắt đầu vào tiểu học).",
                        "Tạo miễn dịch chủ động phòng bệnh sởi, quai bị và rubella ở trẻ em từ 12 tháng tuổi đến 10 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Infranrix IPV + Hib (Bỉ)",
                                           "vac-xin-Infanrix-IPV-Hib.jpg",
                                           new HashSet<>(List.of("2-6 tháng")),
                                           "Vắc xin Infanrix IPV+Hib là vắc xin kết hợp phòng được 5 loại bệnh trong 1 mũi tiêm, bao gồm: Ho gà, bạch hầu, uốn ván, bại liệt và các bệnh viêm phổi, viêm màng não mủ do H. Influenzae týp B (Hib). Tích hợp trong một loại vắc xin, Infanrix IPV+Hib giúp giảm số mũi tiêm, đồng nghĩa với việc hạn chế đau đớn cho bé khi phải tiêm quá nhiều. ",
                                           " Glaxosmithkline (GSK) – Bỉ",
                                           "Vắc xin Infanrix IPV+Hib được chỉ định tiêm bắp sâu (ở mặt trước-bên đùi). ",
                                           "Trẻ quá mẫn cảm với bất kỳ thành phần nào của vắc xin Infanrix IPV+Hib.;Trẻ đã từng có phản ứng quá mẫn sau mũi tiêm vắc xin bạch hầu, ho gà, uốn ván, bại liệt bất hoạt hoặc Hib trước đó.;Trẻ mắc các bệnh về não nhưng không rõ nguyên nhân, xảy ra trong vòng 7 ngày sau khi tiêm vắc xin ho gà trước đó.;Không khuyến cáo sử dụng cho người lớn, trẻ vị thành niên và trẻ em từ 5 tuổi trở lên.",
                                           "Tại chỗ tiêm: đau, đỏ, sưng thường diễn ra từ 1-4 ngày sau tiêm.;Toàn thân: sốt, kích thích, chán ăn.",
                                           5, 5,
                                           "Vắc xin Infanrix IPV+Hib được bảo quản ở nhiệt độ 2-8 độ C. Không được để thành phần Infanrix IPV đông băng. Tránh ánh sáng.",
                                           "Lịch tiêm vắc xin 5 trong 1 Infanrix IPV+Hib gồm 5 mũi:;Mũi 1: Lần đầu tiên.lMũi 2: 1 tháng sau mũi 1.;Mũi 3: 1 tháng sau mũi 2.;Mũi 4: 1 năm sau mũi 3 (tối thiểu 6 tháng sau mũi 3).;Mũi nhắc: 3 năm sau mũi 4.",
                                           "Vắc xin Infanrix IPV+Hib được chỉ định tiêm cho trẻ từ 2 tháng tuổi đến trước sinh nhật 5 tuổi."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Menactra (Mỹ)",
                                           "vac-xin-Menactra.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("7-12 tháng", "4-6 tuổi", "9-18 tuổi",
                                                                 "Người trưởng thành")),
                                           "Vắc xin Menactra được chỉ định để tạo miễn dịch chủ động cơ bản và nhắc lại phòng bệnh xâm lấn do N.meningitidis (vi khuẩn não mô cầu) các nhóm huyết thanh A, C, Y, W-135 gây ra, như: viêm màng não, nhiễm trùng huyết, viêm phổi…",
                                           "Vắc xin Menactra được sản xuất bởi hãng vắc xin hàng đầu thế giới – Sanofi Pasteur (Pháp). Được sản xuất tại Mỹ.",
                                           "Menactra được chỉ định tiêm bắp, tốt nhất là ở mặt trước – ngoài của đùi hoặc vùng cơ delta tùy theo tuổi và khối cơ của đối tượng. Không được tiêm tĩnh mạch hoặc tiêm trong da & dưới da đối với vắc xin Menactra.",
                                           "Người đã bị phản ứng quá mẫn toàn thân với bất cứ thành phần của vắc xin, hoặc sau một lần tiêm vắc xin này hoặc một vắc xin chứa cùng một thành phần trước đây.;Sốt hay bệnh cấp tính: thông thường, trong trường hợp sốt vừa hoặc nặng và/hoặc bệnh cấp tính nên trì hoãn tiêm chủng.",
                                           "Tại chỗ tiêm: đau, sưng, đỏ tại vị trí tiêm;Toàn thân: ngủ gà, cáu kỉnh, nhức đầu, mệt mỏi, tiêu chảy, chán ăn. Các triệu chứng thường nhẹ và thoáng qua.",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C. Không được đông băng.",
                                           "Trẻ từ 9 tháng đến dưới 24 tháng: 2 liều cách nhau ít nhất 3 tháng;2 mũi cơ bản cách nhau ít nhất 3 tháng;Mũi tiêm nhắc: cho Khách hàng từ 15 đến < 56 tuổi;Trẻ em từ 2 tuổi đến 55 tuổi (trước sinh nhật lần thứ 56): ;Mũi 1: lần đầu tiên;Mũi 2 (mũi nhắc): cách mũi 1 ít nhất 4 năm (cho người từ 15 đến 55 tuổi);Người có nguy cơ cao mắc Não mô cầu (có bệnh lý nền, dùng thuốc ức chế miễn dịch…)*:;Trẻ dưới 7 tuổi:;2 mũi cơ bản cách nhau ít nhất 2 tháng;Tiêm 1 mũi nhắc sau mũi cơ bản cuối cùng ít nhất 3 năm;Sau đó mỗi 5 năm;Người tròn 7 tuổi trở lên và người lớn:;2 mũi cơ bản cách nhau ít nhất 2 tháng;Sau đó mỗi 5 năm",
                                           "Vắc xin Menactra (Mỹ) được chỉ định tiêm cho trẻ từ 9 tháng đến 55 tuổi (trước sinh nhật lần thứ 56)."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Engerix B (Bỉ)",
                                           "vac-xin-engerix-b.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("0-2 tháng", "2-6 tháng", "Phụ nữ trước mang thai")),
                                           "Vắc xin Engerix B phòng bệnh do virus viêm gan B – loại virus này có thể lây truyền qua đường máu, qua quan hệ tình dục và từ mẹ truyền sang con.",
                                           "Glaxosmithkline (GSK) – Bỉ",
                                           "Engerix B được chỉ định tiêm bắp (vùng cơ delta). Ngoại lệ với những bệnh nhân bị rối loạn chảy máu hay giảm tiểu cầu có thể tiêm dưới da.",
                                           "Không nên dùng Engerix B cho những đối tượng được biết là quá mẫn cảm với một trong các thành phần của vắc xin, hoặc những đối tượng có biểu hiện mẫn cảm với vắc xin ở lần tiêm trước.;Nhiễm HIV không được xem là chống chỉ định đối với việc chủng ngừa viêm gan B.",
                                           "Tại chỗ tiêm: đau, sưng nhẹ.;Toàn thân: sốt nhẹ, trẻ quấy khóc.",
                                           3, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C. Không để đông băng.",
                                           "Vắc xin Engerix B phòng bệnh viêm gan B có lịch tiêm 3 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: 1 tháng sau mũi 1.;Mũi 3: 5 tháng sau mũi 2.",
                                           "Vắc xin Engerix B 10mcg/0.5ml phòng ngừa viêm gan B được dùng để chủng ngừa cho trẻ sơ sinh đến dưới 20 tuổi. Vắc xin Engerix B 20mcg/1ml phòng ngừa viêm gan B được dùng để chủng ngừa cho người từ 20 tuổi trở lên."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Euvax B (Hàn Quốc)",
                                           "EUVAX.jpg",
                                           new HashSet<>(
                                                   Arrays.asList("0-2 tháng", "2-6 tháng", "Phụ nữ trước mang thai")),
                                           "Vắc xin Euvax B phòng bệnh do virus viêm gan B – loại virus này có thể lây truyền qua đường máu, qua quan hệ tình dục và từ mẹ truyền sang con.",
                                           "Nghiên cứu và phát triển bởi Sanofi Pasteur (Pháp). Vắc xin Euvax sản xuất tại Hàn Quốc.",
                                           "Euvax B chỉ dùng đường tiêm bắp, không nên tiêm ở vùng mông, và không được tiêm tĩnh mạch.;Phải lắc kỹ trước khi dùng, bởi vì trong quá trình bảo quản vắc xin có thể trở thành dạng chất lắng trắng mịn với dịch nổi bên trên trong suốt không màu.",
                                           "Người mẫn cảm với bất kỳ thành phần nào của Euvax B.",
                                           "Tại chỗ tiêm: đau, sưng nhẹ.;Toàn thân: sốt nhẹ, trẻ quấy khóc.",
                                           3, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C. Không được để đông băng.",
                                           "Vắc xin Euvax B phòng bệnh viêm gan B có lịch tiêm 3 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: 1 tháng sau mũi 1.;Mũi 3: 6 tháng sau mũi 1.",
                                           "Vắc xin Euvax B 0.5ml phòng ngừa viêm gan B được dùng để chủng ngừa cho trẻ sơ sinh đến dưới 16 tuổi. Vắc xin Euvax B 1ml phòng ngừa viêm gan B được dùng để chủng ngừa cho người từ 16 tuổi trở lên."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Jevax (Việt Nam)",
                                           "JEVAX.jpg",
                                           new HashSet<>(Arrays.asList("7-12 tháng", "4-6 tuổi")),
                                           "Jevax là vắc xin phòng viêm não Nhật Bản được chỉ định cho trẻ em từ 12 tháng tuổi trở lên và người lớn.",
                                           "Vabiotech – Việt Nam",
                                           "Tiêm dưới da.",
                                           "Người nhạy cảm với bất kỳ thành phần nào trong vắc xin.;Người bị bệnh tim, gan, thận.;Mệt mỏi, sốt cao hoặc nhiễm trùng tiến triển.;Người đang mắc bệnh tiểu đường hoặc suy dinh dưỡng.;Bệnh ung thư máu và các bệnh ác tính nói chung.;Phụ nữ có thai.;Bệnh quá mẫn.",
                                           "Thường gặp là sưng, đau và nổi ban đỏ tại nơi tiêm. Những phản ứng này thường sẽ tự hết sau 1-2 ngày.;Phản ứng toàn thân thường gặp như sốt, đau đầu, buồn nôn, chóng mặt có thể xảy ra với một số người.",
                                           3, 3,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 đến 8 độ C và không được đông băng.",
                                           "Lịch tiêm 3 mũi:;Mũi 1: lần tiêm đầu tiên.;Mũi 2: một – hai tuần sau mũi 1.;Mũi 3: 1 năm sau mũi 2.;* Sau 3 năm tiêm nhắc lại một liều để duy trì miễn dịch hoặc những người có thể trạng miễn dịch tốt thì tiêm nhắc lại trước khi có dịch viêm não xảy ra.",
                                           "Jevax là vắc xin được chỉ định để phòng viêm não Nhật bản cho trẻ em từ 12 tháng tuổi trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Typhoid VI (Việt Nam)",
                                           "vac-xin-Typhoid-Vi.jpg",
                                           new HashSet<>(List.of("13-24 tháng")),
                                           "Vắc xin Typhoid VI phòng ngừa bệnh Thương hàn (bệnh về đường tiêu hóa) gây ra bởi vi khuẩn thương hàn (Salmonella typhi) cho trẻ từ trên 2 tuổi và người lớn. ",
                                           "Viện Pasteur Đà Lạt (DAVAC) - Việt Nam",
                                           "Vắc xin phòng bệnh thương hàn Typhoid Vi được chỉ định tiêm dưới da hoặc tiêm bắp với liều 0.5ml.",
                                           "Không sử dụng Typhoid Vi cho trường hợp có tiền sử dị ứng với một trong các thành phần của vắc xin.;Phụ nữ đang mang thai, trường hợp bắt buộc phải tiêm, cần hỏi ý kiến của bác sĩ.;Trong trường hợp bị sốt hoặc mắc bệnh cấp tính, nên hoãn tiêm vắc xin.",
                                           "Phản ứng tại chỗ: sưng, đau, có quầng đỏ tại vết tiêm.;Phản ứng toàn thân: sốt, đau đầu, đau khớp",
                                           1, 1,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 độ C đến 8 độ C. Không được đóng băng.",
                                           "Lịch tiêm 01 mũi.;Tiêm nhắc: mỗi 3 năm nếu có nguy cơ nhiễm bệnh.",
                                           "Vắc xin thương hàn được chỉ định cho trẻ từ trên 2 tuổi (2 tuổi 1 ngày) trở lên và người lớn."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin STAMARIL (Pháp)",
                                           "STAMARIL-vacxin-phong-benh-sot-vang.jpg",
                                           new HashSet<>(List.of("Ngoài các nhóm trên")),
                                           "Vắc xin Stamaril là vắc xin duy nhất cung cấp miễn dịch bảo vệ hiệu quả cao và tạo miễn dịch chủ động suốt đời khỏi virus thuộc họ Flaviviridae gây bệnh Sốt vàng nguy hiểm. ",
                                           "Sanofi Pasteur (Pháp)",
                                           "Trẻ dưới 12 tháng tuổi: Tiêm mặt trước của đùi.;Trẻ từ 12 – 35 tháng tuổi: Tiêm mặt trước của đùi hoặc cơ delta.;Trẻ từ 36 tháng tuổi trở lên và người lớn: Tiêm ở cơ delta.",
                                           "Vắc xin phòng bệnh sốt vàng chống chỉ định với những người có tiền sử dị ứng với protein của trứng gà, thịt gà, và các thành phần có trong vắc xin.;Chống chỉ định với người bị suy giảm, rối loạn chức năng tuyến ức.;Chống chỉ định với người suy giảm miễn dịch (do bẩm sinh, mắc phải, thuốc, xạ trị), người nhiễm HIV.;Không tiêm vắc xin cho phụ nữ có thai và cho con bú.",
                                           "Đau tại vết tiêm, đôi khi sưng và ửng đỏ.;Sốt nhẹ và nôn.",
                                           1, 1,
                                           "Bảo quản ở nhiệt độ lạnh (từ 2 – 8 độ C). Không được đóng băng.",
                                           "Lịch tiêm cơ bản: Tiêm 1 mũi duy nhất vắc xin Stamaril 0.5ml (sau khi đã pha hồi chỉnh).;Lịch tiêm nhắc: Cân nhắc tiêm nhắc lại vắc xin Stamaril 0.5ml mỗi 10 năm đối với:;Người không tạo đủ đáp ứng miễn dịch sau khi tiêm mũi đầu.;Khuyến cáo chính thức của các Cơ quan Quản lý Y tế địa phương và điều kiện nhập cảnh ở một số Quốc gia.",
                                           "Vắc xin Stamaril được chỉ định tiêm cho trẻ từ 9 tháng tuổi đến người lớn 60 tuổi phòng bệnh sốt vàng."));

        vaccineCreationRequestList.add(
                new VaccineCreationRequest("Vắc xin Qdenga (Sản xuất tại Đức)",
                                           "vaccine-qdenga.jpg",
                                           new HashSet<>(Arrays.asList("4-6 tuổi", "")),
                                           "Vắc xin Qdenga là chế phẩm sinh học đặc biệt có khả năng phòng bệnh sốt xuất huyết do virus Dengue gây ra, có khả năng bảo vệ chống lại cả 4 nhóm huyết thanh của virus dengue, bao gồm DEN-1, DEN-2, DEN-3 và DEN-4, được chỉ định tiêm cho người từ 4 tuổi trở lên với hiệu lực bảo vệ hơn 80% nguy cơ mắc bệnh do 4 tuýp virus Dengue và trên 90% nguy cơ nhập viện, mắc bệnh nặng và biến chứng nguy hiểm do bệnh sốt xuất huyết gây ra.",
                                           "Nghiên cứu, phát triển và sản xuất bởi Hãng vắc xin và dược phẩm Takeda – Nhật Bản, xuất xứ tại Đức",
                                           "Sau khi hoàn nguyên hoàn toàn vắc xin đông khô với chất pha loãng (dung môi), Qdenga nên được tiêm dưới da, tốt nhất là ở cánh tay trên ở vùng cơ delta.;Qdenga không được tiêm vào mạch, không được tiêm trong da hoặc tiêm bắp.;Không nên trộn vắc xin trong cùng một ống tiêm với bất kỳ loại vắc xin hoặc sản phẩm thuốc tiêm nào khác.",
                                           "Quá mẫn cảm với hoạt chất hoặc với bất kỳ tá dược nào hoặc quá mẫn cảm với liều Qdenga trước đó;Những người bị suy giảm miễn dịch bẩm sinh hoặc mắc phải, bao gồm sử dụng các liệu pháp ức chế miễn dịch;Những người nhiễm HIV có triệu chứng hoặc nhiễm HIV không có triệu chứng kèm theo bằng chứng suy giảm chức năng hệ miễn dịch;Phụ nữ có thai, phụ nữ cho con bú",
                                           "Triệu chứng tại chỗ tiêm: đau tại chỗ tiêm, đau cơ, ban đỏ;Triệu chứng toàn thân: nhức đầu, khó chịu, suy nhược, sốt",
                                           2, 2,
                                           "Vắc xin được bảo quản ở nhiệt độ từ 2 –  8°C. Tránh tiếp xúc với chất bảo quản, thuốc sát trùng, chất tẩy rửa và các chất chống vi rút khác vì chúng có thể làm bất hoạt vắc xin. Chỉ sử dụng ống tiêm vô trùng không chứa chất bảo quản, chất khử trùng, chất tẩy rửa và các chất chống virus khác để pha và tiêm Qdenga. Qdenga phải được hoàn nguyên trước khi dùng.",
                                           "Người từ 4 tuổi trở lên, áp dụng lịch tiêm 2 mũi:;Mũi 1: lần tiêm đầu tiên trong độ tuổi.;Mũi 2: cách mũi đầu tiên 3 tháng.",
                                           "Vắc xin Qdenga được chỉ định phòng ngừa bệnh sốt xuất huyết cho bất kỳ loại huyết thanh virus sốt xuất huyết nào gây ra ở những người từ 4 tuổi trở lên."));

        try {
            for (VaccineCreationRequest vaccineCreationRequest : vaccineCreationRequestList) {
                Vaccine vaccine = vaccineMapper.toVaccine(
                        vaccineCreationRequest);
                vaccineRepository.save(vaccine);
                String vaccineCode = "VAC" + vaccine.getVaccineId().toString();
                vaccine.setVaccineCode(vaccineCode);
                vaccine.setVaccineCreateAt(createDateTime);
                vaccine.setVaccineUpdateAt(createDateTime);
                vaccineRepository.save(vaccine);
            }
            System.out.println("Vaccine Data Inserted Successfully!");
        }
        catch (Exception ex) {
            System.out.println("Vaccine Data Inserted Failed!");
        }
    }

    public void updateDiseaseVaccineRelations() {
        List<Long> diseaseIds = LongStream.rangeClosed(1, 30).boxed().toList();
        Map<Long, List<Long>> diseaseVaccineMap = new HashMap<>();
        diseaseVaccineMap.put(1L, List.of(1016L));
        diseaseVaccineMap.put(2L, Arrays.asList(1014L, 1015L));
        diseaseVaccineMap.put(3L, Arrays.asList(1019L, 1020L, 1021L));
        diseaseVaccineMap.put(4L, Arrays.asList(1013L, 1006L, 1000L));
        diseaseVaccineMap.put(5L, List.of(1022L));
        diseaseVaccineMap.put(6L, Arrays.asList(1010L, 1017L, 1046L, 1047L));
        diseaseVaccineMap.put(7L, List.of(1002L));
        diseaseVaccineMap.put(8L, List.of(1023L));
        diseaseVaccineMap.put(9L, List.of(1045L));
        diseaseVaccineMap.put(10L, List.of(1025L));
        diseaseVaccineMap.put(11L, Arrays.asList(1018L, 1026L, 1043L));
        diseaseVaccineMap.put(12L, Arrays.asList(1008L, 1041L, 1042L));
        diseaseVaccineMap.put(13L, List.of());
        diseaseVaccineMap.put(14L, Arrays.asList(1005L, 1012L, 1024L, 1040L));
        diseaseVaccineMap.put(15L, List.of(1011L));
        diseaseVaccineMap.put(16L, List.of(1001L));
        diseaseVaccineMap.put(17L, List.of(1051L));
        diseaseVaccineMap.put(18L, Arrays.asList(1033L, 1038L));
        diseaseVaccineMap.put(19L, Arrays.asList(1029L, 1009L, 1048L));
        diseaseVaccineMap.put(20L, Arrays.asList(1004L, 1003L));
        diseaseVaccineMap.put(21L, Arrays.asList(1028L, 1007L));
        diseaseVaccineMap.put(22L, List.of(1027L, 1044L));
        diseaseVaccineMap.put(23L, List.of(1037L));
        diseaseVaccineMap.put(24L, List.of(1032L));
        diseaseVaccineMap.put(25L, Arrays.asList(1031L, 1030L));
        diseaseVaccineMap.put(26L, Arrays.asList(1034L, 1049L));
        diseaseVaccineMap.put(27L, List.of(1036L));
        diseaseVaccineMap.put(28L, List.of(1035L));
        diseaseVaccineMap.put(29L, List.of(1050L));
        diseaseVaccineMap.put(30L, List.of(1039L));
        for (Long diseaseId : diseaseIds) {
            Disease disease = diseaseService.getDiseaseById(diseaseId);
            List<Long> vaccineIds = diseaseVaccineMap.get(diseaseId);
            List<Vaccine> vaccines = getAllVaccinesByListId(vaccineIds);
            vaccines.forEach(vaccine -> vaccine.setDisease(disease));
            insertAllVaccines(vaccines);
        }
    }


}
