package vcms.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.VaccineBatchCreationRequest;
import vcms.model.VaccineBatch;
import vcms.service.VaccineBatchService;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/batches")
public class VaccineBatchController {

    private final VaccineBatchService vaccineBatchService;


    public VaccineBatchController(VaccineBatchService vaccineBatchService) {
        this.vaccineBatchService = vaccineBatchService;
    }

    @PostMapping("/add")
    public ResponseEntity<VaccineBatch> addNewBatch(
            @RequestParam("vaccineBatchNumber") String vaccineBatchNumber,
            @RequestParam("vaccineBatchImportDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate vaccineBatchImportDate,
            @RequestParam("vaccineBatchQuantity") int vaccineBatchQuantity,
            @RequestParam("vaccineBatchValue") int vaccineBatchValue,
            @RequestParam("batchDetailFile") MultipartFile batchDetailFile) throws IOException {

        VaccineBatchCreationRequest vaccineBatchCreationRequest = new VaccineBatchCreationRequest();
        vaccineBatchCreationRequest.setVaccineBatchNumber(vaccineBatchNumber);
        vaccineBatchCreationRequest.setVaccineBatchImportDate(
                vaccineBatchImportDate);
        vaccineBatchCreationRequest.setVaccineBatchQuantity(
                vaccineBatchQuantity);
        vaccineBatchCreationRequest.setVaccineBatchValue(vaccineBatchValue);
        vaccineBatchCreationRequest.setBatchDetailFile(batchDetailFile);

        VaccineBatch savedBatch = vaccineBatchService.addNewVaccineBatch(
                vaccineBatchCreationRequest);

        return ResponseEntity.ok(savedBatch);
    }
}
