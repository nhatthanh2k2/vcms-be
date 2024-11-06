package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.UpdateVaccinePriceRequest;
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

    public void updateVaccinePrice(UpdateVaccinePriceRequest request) {
        BatchDetail batchDetail = batchDetailRepository.findById(request.getBatchDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        batchDetail.setBatchDetailVaccinePrice(request.getNewPrice());
        batchDetailRepository.save(batchDetail);
    }

    public void insertBatchDetailList(List<BatchDetail> batchDetailList) {
        batchDetailRepository.saveAll(batchDetailList);
    }

    public void saveBatchDetail(BatchDetail batchDetail) {
        batchDetailRepository.save(batchDetail);
    }
}
