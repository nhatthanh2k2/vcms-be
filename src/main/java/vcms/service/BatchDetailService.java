package vcms.service;

import org.springframework.stereotype.Service;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.model.BatchDetail;
import vcms.model.Vaccine;
import vcms.model.VaccineBatch;
import vcms.repository.BatchDetailRepository;

import java.util.List;

@Service
public class BatchDetailService {
    private final BatchDetailRepository batchDetailRepository;

    public BatchDetailService(BatchDetailRepository batchDetailRepository) {
        this.batchDetailRepository = batchDetailRepository;
    }

    public BatchDetail getBatchDetailById(Long batchDetailId) {
        return batchDetailRepository.findById(batchDetailId).orElseThrow(() -> new AppException(
                ErrorCode.NOT_EXISTED));
    }

    public List<BatchDetail> getBatchDetailByVaccine(Vaccine vaccine) {
        return batchDetailRepository.findByVaccine(vaccine);
    }

    public List<BatchDetail> getAllBatchDetailByBatchDetailIdList(List<Long> batchDetailIds) {
        return batchDetailRepository.findAllById(batchDetailIds);
    }

    public List<BatchDetail> getAllBatchDetailByVaccineBatch(VaccineBatch batch) {
        return batchDetailRepository.findAllByVaccineBatch(batch);
    }

    public void insertBatchDetailList(List<BatchDetail> batchDetailList) {
        batchDetailRepository.saveAll(batchDetailList);
    }
}
