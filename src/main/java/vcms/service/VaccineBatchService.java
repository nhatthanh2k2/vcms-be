package vcms.service;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
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


    @Transactional(rollbackFor = AppException.class)
    public VaccineBatch insertVaccineBatch(VaccineBatchCreationRequest request) throws IOException {

        VaccineBatch vaccineBatch = vaccineBatchMapper.toVaccineBatch(request);
        vaccineBatch.setVaccineBatchImportDate(dateService.getDateNow());
        vaccineBatchRepository.save(vaccineBatch);

        MultipartFile file = request.getBatchDetailFile();
        Map<String, Object> result = readBatchDetailsFromExcel(file, vaccineBatch);

        List<BatchDetail> batchDetails = (List<BatchDetail>) result.get("batchDetails");

        int uniqueVaccineCount = (int) result.get("uniqueVaccineCount");
        BigInteger totalCost = (BigInteger) result.get("totalCost");
        vaccineBatch.setVaccineBatchQuantity(uniqueVaccineCount);
        vaccineBatch.setVaccineBatchValue(totalCost);
        vaccineBatchRepository.save(vaccineBatch);
        batchDetailService.insertBatchDetailList(batchDetails);

        return vaccineBatch;
    }

    private Map<String, Object> readBatchDetailsFromExcel(MultipartFile file,
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
        Set<String> uniqueVaccineCodes = new HashSet<>();
        BigInteger totalCost = BigInteger.ZERO;
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

            Vaccine vaccine = vaccineService.getVaccineByVaccineCode(vaccineCode);
            if (vaccine == null) {
                throw new AppException(ErrorCode.NOT_EXISTED);
            }

            batchDetail.setVaccine(vaccine);
            batchDetail.setVaccineBatch(vaccineBatch);
            batchDetail.setBatchDetailInitialQuantity(quantity);
            batchDetail.setBatchDetailRemainingQuantity(quantity);
            batchDetail.setBatchDetailVaccinePrice(price);
            batchDetail.setBatchDetailManufactureDate(manufactureDate);
            batchDetail.setBatchDetailExpirationDate(expirationDate);
            batchDetails.add(batchDetail);

            if (!exampleVaccineCodeList.contains(vaccineCode)) {
                BatchDetail exampleBatchDetail = new BatchDetail();
                exampleBatchDetail.setVaccine(vaccine);
                exampleBatchDetail.setVaccineBatch(exampleBatch);
                exampleBatchDetail.setBatchDetailInitialQuantity(quantity);
                exampleBatchDetail.setBatchDetailRemainingQuantity(quantity);
                exampleBatchDetail.setBatchDetailVaccinePrice(price);
                exampleBatchDetail.setBatchDetailManufactureDate(manufactureDate);
                exampleBatchDetail.setBatchDetailExpirationDate(expirationDate);
                batchDetailService.saveBatchDetail(exampleBatchDetail);
                exampleVaccineCodeList.add(vaccineCode);
            }
            BigInteger itemCost = BigInteger.valueOf(quantity).multiply(BigInteger.valueOf(price));
            totalCost = totalCost.add(itemCost);
            uniqueVaccineCodes.add(vaccineCode);
        }

        workbook.close();

        Map<String, Object> result = new HashMap<>();
        result.put("batchDetails", batchDetails);
        result.put("uniqueVaccineCount", uniqueVaccineCodes.size());
        result.put("totalCost", totalCost);
        return result;
    }


    private LocalDate getLocalDateFromCell(XSSFCell cell, DateTimeFormatter formatter) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        else if (cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue(), formatter);
            }
            catch (DateTimeParseException e) {
                //System.err.println("Lỗi định dạng ngày: " + cell.getStringCellValue());
                return null;
            }
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
