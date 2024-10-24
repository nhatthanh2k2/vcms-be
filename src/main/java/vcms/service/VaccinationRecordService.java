package vcms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.request.VaccinationRecordCreationRequest;
import vcms.dto.response.VaccinationRecordResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.*;
import vcms.model.*;
import vcms.repository.VaccinationRecordRepository;
import vcms.utils.DateService;
import vcms.utils.GenerateService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VaccinationRecordService {
    private final VaccinationRecordRepository vaccinationRecordRepository;

    private final CustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;

    private final VaccineBatchService vaccineBatchService;

    private final VaccineBatchMapper vaccineBatchMapper;

    private final VaccinePackageService vaccinePackageService;

    private final VaccinePackageMapper vaccinePackageMapper;

    private final VaccineService vaccineService;

    private final VaccineMapper vaccineMapper;

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    private final VaccinationRecordMapper vaccinationRecordMapper;

    private final BatchDetailService batchDetailService;

    private final DateService dateService;

    @Autowired
    private GenerateService generateService;

    public VaccinationRecordService(VaccinationRecordRepository vaccinationRecordRepository,
                                    CustomerService customerService, VaccineBatchMapper vaccineBatchMapper,
                                    VaccinePackageMapper vaccinePackageMapper, VaccineMapper vaccineMapper,
                                    VaccinationRecordMapper vaccinationRecordMapper,
                                    VaccineBatchService vaccineBatchService,
                                    VaccinePackageService vaccinePackageService, VaccineService vaccineService,
                                    EmployeeService employeeService, EmployeeMapper employeeMapper,
                                    BatchDetailService batchDetailService, DateService dateService) {
        this.vaccinationRecordRepository = vaccinationRecordRepository;
        this.customerService = customerService;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.vaccineMapper = vaccineMapper;
        this.vaccinationRecordMapper = vaccinationRecordMapper;
        this.vaccineBatchService = vaccineBatchService;
        this.vaccinePackageService = vaccinePackageService;
        this.vaccineService = vaccineService;
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
        this.batchDetailService = batchDetailService;
        this.dateService = dateService;
    }

    public List<VaccinationRecordResponse> getAllRecordOfCustomer(LookupCustomerRequest request) {
        Customer customer = customerService.findCustomerByIdentifierAndDob(
                request.getCustomerIdentifier(), request.getCustomerDob());
        List<VaccinationRecord> vaccinationRecordList = vaccinationRecordRepository.findAllByCustomer(customer);
        List<VaccinationRecordResponse> vaccinationRecordResponseList = new ArrayList<>();
        for (VaccinationRecord record : vaccinationRecordList) {
            VaccinationRecordResponse recordResponse = vaccinationRecordMapper.toVaccinationRecordResponse(record);
            vaccinationRecordResponseList.add(recordResponse);
        }
        return vaccinationRecordResponseList;
    }

    public boolean checkVaccineQuantityAvailability(List<BatchDetail> batchDetailList, Vaccine vaccine) {
        for (BatchDetail detail : batchDetailList) {
            if (detail.getVaccine().equals(vaccine) && detail.getBatchDetailVaccineQuantity() >= 1) {
                detail.setBatchDetailVaccineQuantity(detail.getBatchDetailVaccineQuantity() - 1);
                batchDetailService.saveBatchDetail(detail);
                return true;
            }
        }
        throw new AppException(ErrorCode.VACCINE_QUANTITY_INSUFFICIENT);
    }

    public VaccinationRecordResponse createVaccinationRecord(VaccinationRecordCreationRequest request) {
        try {
            VaccinationRecord vaccinationRecord = vaccinationRecordMapper.toVaccinationRecord(request);
            Customer customer = customerService.findCustomerByIdentifierAndDob(request.getCustomerPhone(),
                                                                               request.getCustomerDob());
            Employee employee = employeeService.getEmployeeByUsername(request.getEmployeeUsername());
            Vaccine vaccine = vaccineService.getVaccineByVaccineId(request.getVaccineId());
            VaccinePackage vaccinePackage;
            VaccineBatch vaccineBatch = vaccineBatchService.getBatchById(request.getVaccineBatchId());
            List<BatchDetail> batchDetailList = vaccineBatchService.getBatchDetailListByBatchId(
                    vaccineBatch.getVaccineBatchId());
            checkVaccineQuantityAvailability(batchDetailList, vaccine);
            String code = "";
            boolean isCodeUnique = false;
            while (!isCodeUnique) {
                code = "VR" + generateService.generateRandomNumber();
                // Kiểm tra xem mã đã tồn tại trong DB hay chưa
                isCodeUnique = !vaccinationRecordRepository.existsByVaccinationRecordCode(code);
            }
            if (request.getVaccinationRecordType().equalsIgnoreCase("SINGLE")) {
                vaccinePackage = null;
            }
            else {
                vaccinePackage = vaccinePackageService.getVaccinePackageById(request.getVaccinePackageId());
            }
            vaccinationRecord.setVaccinationRecordCode(code);
            vaccinationRecord.setVaccinationRecordDate(dateService.getDateNow());
            vaccinationRecord.setCustomer(customer);
            vaccinationRecord.setEmployee(employee);
            vaccinationRecord.setVaccineBatch(vaccineBatch);
            vaccinationRecord.setVaccine(vaccine);
            vaccinationRecord.setVaccinePackage(vaccinePackage);
            return vaccinationRecordMapper.toVaccinationRecordResponse(
                    vaccinationRecordRepository.save(vaccinationRecord));
        }
        catch (Exception exception) {
            throw new AppException(ErrorCode.CREATE_FAILED);
        }
    }

    public List<VaccinationRecordResponse> getAllVaccinationRecordByCreateDate(LocalDate createDate) {
        List<VaccinationRecord> vaccinationRecordList = vaccinationRecordRepository
                .findAllByVaccinationRecordDate(createDate);
        List<VaccinationRecordResponse> vaccinationRecordResponseList = new ArrayList<>();
        for (VaccinationRecord record : vaccinationRecordList) {
            VaccinationRecordResponse response = vaccinationRecordMapper.toVaccinationRecordResponse(record);
            response.setCustomerResponse(customerMapper.toCustomerResponse(record.getCustomer()));
            response.setEmployeeFullName(record.getEmployee().getEmployeeFullName());
            response.setVaccineName(record.getVaccine().getVaccineName());
            VaccinePackage vaccinePackage = record.getVaccinePackage();
            if (vaccinePackage == null) {
                response.setVaccinePackageName("Không có");
            }
            else response.setVaccinePackageName(record.getVaccinePackage().getVaccinePackageName());
            response.setVaccineBatchNumber(record.getVaccineBatch().getVaccineBatchNumber());
            vaccinationRecordResponseList.add(response);
        }
        return vaccinationRecordResponseList;
    }

}
