package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccinationRecordResponse;
import vcms.mapper.VaccinationRecordMapper;
import vcms.mapper.VaccineBatchMapper;
import vcms.mapper.VaccineMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.Customer;
import vcms.model.VaccinationRecord;
import vcms.repository.VaccinationRecordRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class VaccinationRecordService {
    private final VaccinationRecordRepository vaccinationRecordRepository;

    private final CustomerService customerService;

    private final VaccineBatchMapper vaccineBatchMapper;

    private final VaccinePackageMapper vaccinePackageMapper;

    private final VaccineMapper vaccineMapper;

    private final VaccinationRecordMapper vaccinationRecordMapper;

    public VaccinationRecordService(VaccinationRecordRepository vaccinationRecordRepository,
                                    CustomerService customerService, VaccineBatchMapper vaccineBatchMapper,
                                    VaccinePackageMapper vaccinePackageMapper, VaccineMapper vaccineMapper,
                                    VaccinationRecordMapper vaccinationRecordMapper) {
        this.vaccinationRecordRepository = vaccinationRecordRepository;
        this.customerService = customerService;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.vaccineMapper = vaccineMapper;
        this.vaccinationRecordMapper = vaccinationRecordMapper;
    }

    public List<VaccinationRecordResponse> getAllRecordOfCustomer(LookupCustomerRequest request) {

        Customer customer = customerService.findCustomerByIdentifierAndDob(
                request.getCustomerIdentifier(), request.getCustomerDob());
        List<VaccinationRecord> vaccinationRecordList = vaccinationRecordRepository.findAllByCustomer(customer);
        List<VaccinationRecordResponse> vaccinationRecordResponseList = new ArrayList<>();
        for (VaccinationRecord record : vaccinationRecordList) {
            VaccinationRecordResponse recordResponse = vaccinationRecordMapper.toVaccinationRecordResponse(record);
            BatchDetailResponse batchDetailResponse = vaccineBatchMapper.toBatchDetailResponse(record.getBatchDetail());
            batchDetailResponse.setVaccineResponse(
                    vaccineMapper.toVaccineResponse(record.getBatchDetail().getVaccine()));
            recordResponse.setBatchDetailResponse(batchDetailResponse);
            recordResponse.setVaccinePackageResponse(vaccinePackageMapper.toVaccinePackageResponse(
                    record.getVaccinePackage()));
            vaccinationRecordResponseList.add(recordResponse);
        }
        return vaccinationRecordResponseList;
    }
}
