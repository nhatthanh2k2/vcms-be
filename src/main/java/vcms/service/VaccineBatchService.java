package vcms.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.VaccineBatchCreationRequest;
import vcms.enums.VaccineType;
import vcms.model.BatchDetail;
import vcms.model.Vaccine;
import vcms.model.VaccineBatch;
import vcms.repository.BatchDetailRepository;
import vcms.repository.VaccineBatchRepository;
import vcms.repository.VaccineRepository;
import vcms.utils.DateService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VaccineBatchService {

    private final VaccineBatchRepository vaccineBatchRepository;

    private final VaccineRepository vaccineRepository;

    private final DateService dateService;

    private final BatchDetailRepository batchDetailRepository;

    public VaccineBatchService(VaccineBatchRepository vaccineBatchRepository,
                               DateService dateService,
                               BatchDetailRepository batchDetailRepository,
                               VaccineRepository vaccineRepository) {
        this.vaccineBatchRepository = vaccineBatchRepository;
        this.dateService = dateService;
        this.batchDetailRepository = batchDetailRepository;
        this.vaccineRepository = vaccineRepository;
    }

    public VaccineBatch addNewVaccineBatch(
            VaccineBatchCreationRequest vaccineBatchCreationRequest) throws IOException {

        VaccineBatch newVaccineBatch = new VaccineBatch();
        newVaccineBatch.setVaccineBatchNumber(
                vaccineBatchCreationRequest.getVaccineBatchNumber());
        newVaccineBatch.setVaccineBatchImportDate(
                vaccineBatchCreationRequest.getVaccineBatchImportDate());
        newVaccineBatch.setVaccineBatchQuantity(
                vaccineBatchCreationRequest.getVaccineBatchQuantity());
        newVaccineBatch.setVaccineBatchValue(
                vaccineBatchCreationRequest.getVaccineBatchValue());
        vaccineBatchRepository.save(newVaccineBatch);

        MultipartFile file = vaccineBatchCreationRequest.getBatchDetailFile();
        List<BatchDetail> batchDetails = readBatchDetailsFromExcel(file,
                                                                   newVaccineBatch);

        batchDetailRepository.saveAll(batchDetails);

        return newVaccineBatch;
    }

    private List<BatchDetail> readBatchDetailsFromExcel(MultipartFile file,
                                                        VaccineBatch vaccineBatch) throws IOException {
        List<BatchDetail> batchDetails = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);

            BatchDetail batchDetail = new BatchDetail();

            // Lấy dữ liệu từ các ô trong dòng
            String vaccineCode = row.getCell(0).getStringCellValue();
            //String vaccineBatchNumber = row.getCell(1).getStringCellValue();
            int quantity = (int) row.getCell(1).getNumericCellValue();
            int price = (int) row.getCell(2).getNumericCellValue();
            LocalDate manufactureDate = row.getCell(
                    3).getLocalDateTimeCellValue().toLocalDate();
            LocalDate expirationDate = row.getCell(
                    4).getLocalDateTimeCellValue().toLocalDate();
            String vaccineTypeStr = row.getCell(5).getStringCellValue();
            VaccineType vaccineType = VaccineType.valueOf(
                    vaccineTypeStr.toUpperCase());

            Vaccine vaccine = vaccineRepository.findByVaccineCode(vaccineCode);

            // Gán giá trị cho BatchDetail
            batchDetail.setVaccine(vaccine);
            batchDetail.setVaccineBatch(vaccineBatch);
            batchDetail.setBatchDetailVaccineQuantity(quantity);
            batchDetail.setBatchDetailVaccinePrice(price);
            batchDetail.setBatchDetailManufactureDate(manufactureDate);
            batchDetail.setBatchDetailExpirationDate(expirationDate);
            batchDetail.setVaccineType(vaccineType);

            batchDetails.add(batchDetail);
        }

        workbook.close();

        return batchDetails;
    }
}
