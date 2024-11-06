package vcms.service;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.VaccineBatchCreationRequest;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccineBatchResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.DiseaseMapper;
import vcms.mapper.VaccineBatchMapper;
import vcms.mapper.VaccineMapper;
import vcms.model.BatchDetail;
import vcms.model.Vaccine;
import vcms.model.VaccineBatch;
import vcms.repository.VaccineBatchRepository;
import vcms.utils.DateService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VaccineBatchService {

    private final VaccineBatchRepository vaccineBatchRepository;

    private final VaccineService vaccineService;

    private final BatchDetailService batchDetailService;

    private final VaccineBatchMapper vaccineBatchMapper;

    private final VaccineMapper vaccineMapper;

    private final DiseaseMapper diseaseMapper;

    private final DateService dateService;

    public VaccineBatchService(VaccineBatchRepository vaccineBatchRepository,
                               VaccineBatchMapper vaccineBatchMapper,
                               VaccineMapper vaccineMapper,
                               DiseaseMapper diseaseMapper, VaccineService vaccineService,
                               BatchDetailService batchDetailService, DateService dateService) {
        this.vaccineBatchRepository = vaccineBatchRepository;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.vaccineMapper = vaccineMapper;
        this.diseaseMapper = diseaseMapper;
        this.vaccineService = vaccineService;
        this.batchDetailService = batchDetailService;
        this.dateService = dateService;
    }

    public List<VaccineBatchResponse> getAllVaccineBatch() {
        return vaccineBatchRepository.findAll().stream()
                .filter(vaccineBatch -> vaccineBatch.getVaccineBatchId() != 1)
                .map(vaccineBatchMapper::toVaccineBatchResponse)
                .toList();
    }

    public VaccineBatch getBatchById(Long batchId) {
        return vaccineBatchRepository.findById(batchId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
    }

    public List<BatchDetailResponse> getDetailOfSampleBatch() {
        VaccineBatch vaccineBatch = vaccineBatchRepository.findById(1L)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        List<BatchDetail> batchDetailList = batchDetailService.getAllBatchDetailByVaccineBatch(vaccineBatch);
        List<BatchDetailResponse> batchDetailResponseList = new ArrayList<>();
        for (BatchDetail batchDetail : batchDetailList) {
            BatchDetailResponse batchDetailResponse = vaccineBatchMapper.toBatchDetailResponse(
                    batchDetail);
            Vaccine vaccine = batchDetail.getVaccine();
            batchDetailResponse.setVaccineResponse(
                    vaccineMapper.toVaccineResponse(batchDetail.getVaccine()));
            batchDetailResponse.setDiseaseResponse(
                    diseaseMapper.toDiseaseResponse(vaccine.getDisease()));
            batchDetailResponseList.add(batchDetailResponse);
        }
        return batchDetailResponseList;
    }

    public List<BatchDetail> getDetailListOfSampleBatch() {

        VaccineBatch vaccineBatch = vaccineBatchRepository.findById(1L)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        return batchDetailService.getAllBatchDetailByVaccineBatch(vaccineBatch);
    }

    public List<BatchDetail> getDetailListByBatchId(Long batchId) {

        VaccineBatch vaccineBatch = vaccineBatchRepository.findById(batchId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        return batchDetailService.getAllBatchDetailByVaccineBatch(vaccineBatch);
    }

    public List<BatchDetailResponse> getDetailsOfBatch(Long batchId) {

        VaccineBatch vaccineBatch = vaccineBatchRepository.findById(batchId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        List<BatchDetail> batchDetailList = batchDetailService.getAllBatchDetailByVaccineBatch(vaccineBatch);
        List<BatchDetailResponse> batchDetailResponseList = new ArrayList<>();
        for (BatchDetail batchDetail : batchDetailList) {
            BatchDetailResponse batchDetailResponse = vaccineBatchMapper.toBatchDetailResponse(
                    batchDetail);
            Vaccine vaccine = batchDetail.getVaccine();
            batchDetailResponse.setVaccineResponse(
                    vaccineMapper.toVaccineResponse(batchDetail.getVaccine()));
            batchDetailResponse.setDiseaseResponse(
                    diseaseMapper.toDiseaseResponse(vaccine.getDisease()));
            batchDetailResponseList.add(batchDetailResponse);
        }
        return batchDetailResponseList;
    }


    public VaccineBatch insertVaccineBatch(VaccineBatchCreationRequest request) throws IOException {

        VaccineBatch vaccineBatch = vaccineBatchMapper.toVaccineBatch(request);
        vaccineBatch.setVaccineBatchImportDate(dateService.getDateNow());
        vaccineBatchRepository.save(vaccineBatch);

        MultipartFile file = request.getBatchDetailFile();
        List<BatchDetail> batchDetails = readBatchDetailsFromExcel(file, vaccineBatch);

        batchDetailService.insertBatchDetailList(batchDetails);

        return vaccineBatch;
    }


    private List<BatchDetail> readBatchDetailsFromExcel(MultipartFile file,
                                                        VaccineBatch vaccineBatch) throws IOException {
        List<BatchDetail> batchDetails = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        VaccineBatch exampleBatch = vaccineBatchRepository.findById(1L)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        List<BatchDetailResponse> batchDetailResponseList = getDetailsOfBatch(1L);
        Set<String> exampleVaccineCodeList = batchDetailResponseList.stream()
                .map(detail -> detail.getVaccineResponse().getVaccineCode())
                .collect(Collectors.toSet());

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);

            if (row == null || isRowEmpty(row)) {
                continue;
            }

            BatchDetail batchDetail = new BatchDetail();

            String vaccineCode = row.getCell(0).getStringCellValue();
            int quantity = (int) row.getCell(2).getNumericCellValue();
            int price = (int) row.getCell(3).getNumericCellValue();
            LocalDate manufactureDate = getLocalDateFromCell(row.getCell(4),
                                                             formatter);
            LocalDate expirationDate = getLocalDateFromCell(row.getCell(5),
                                                            formatter);
            String vaccineTypeStr = row.getCell(6).getStringCellValue();

            Vaccine vaccine = vaccineService.getVaccineByVaccineCode(vaccineCode);
            if (vaccine == null) {
                throw new AppException(ErrorCode.NOT_EXISTED);
            }

            batchDetail.setVaccine(vaccine);
            batchDetail.setVaccineBatch(vaccineBatch);
            batchDetail.setBatchDetailVaccineQuantity(quantity);
            batchDetail.setBatchDetailVaccinePrice(price);
            batchDetail.setBatchDetailManufactureDate(manufactureDate);
            batchDetail.setBatchDetailExpirationDate(expirationDate);
            batchDetail.setVaccineType(vaccineTypeStr);
            batchDetail.setBatchDetailCreateAt(dateService.getDateTimeNow());
            batchDetail.setBatchDetailUpdateAt(dateService.getDateTimeNow());
            batchDetails.add(batchDetail);

//            if (!exampleVaccineCodeList.contains(vaccineCode)) {
//                BatchDetail exampleBatchDetail = new BatchDetail();
//                exampleBatchDetail.setVaccine(vaccine);
//                exampleBatchDetail.setVaccineBatch(exampleBatch);
//                exampleBatchDetail.setBatchDetailVaccineQuantity(quantity);
//                exampleBatchDetail.setBatchDetailVaccinePrice(price);
//                exampleBatchDetail.setBatchDetailManufactureDate(manufactureDate);
//                exampleBatchDetail.setBatchDetailExpirationDate(expirationDate);
//                exampleBatchDetail.setVaccineType(vaccineTypeStr);
//                exampleBatchDetail.setBatchDetailCreateAt(dateService.getDateTimeNow());
//                exampleBatchDetail.setBatchDetailUpdateAt(dateService.getDateTimeNow());
//
//                batchDetailService.saveBatchDetail(exampleBatchDetail);
//
//                exampleVaccineCodeList.add(vaccineCode);
//            }
        }
        workbook.close();

        return batchDetails;
    }

    private LocalDate getLocalDateFromCell(XSSFCell cell,
                                           DateTimeFormatter formatter) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        else if (cell.getCellType() == CellType.STRING) {
            return LocalDate.parse(cell.getStringCellValue(), formatter);
        }
        return null;
    }

    private boolean isRowEmpty(XSSFRow row) {
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            XSSFCell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
