package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.ScreeningRecordCreationRequest;
import vcms.dto.response.CustomerResponse;
import vcms.dto.response.EmployeeResponse;
import vcms.dto.response.ScreeningRecordResponse;
import vcms.mapper.CustomerMapper;
import vcms.mapper.EmployeeMapper;
import vcms.mapper.ScreeningRecordMapper;
import vcms.model.Customer;
import vcms.model.Employee;
import vcms.model.ScreeningRecord;
import vcms.repository.ScreeningRecordRepository;
import vcms.utils.DateService;
import vcms.utils.GenerateService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScreeningRecordService {
    private final ScreeningRecordRepository screeningRecordRepository;

    private final ScreeningRecordMapper screeningRecordMapper;

    private final CustomerService customerService;

    private final CustomerMapper customerMapper;

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    private final GenerateService generateService;

    private final DateService dateService;

    public ScreeningRecordService(ScreeningRecordRepository screeningRecordRepository,
                                  ScreeningRecordMapper screeningRecordMapper, CustomerService customerService,
                                  CustomerMapper customerMapper, EmployeeService employeeService,
                                  EmployeeMapper employeeMapper, GenerateService generateService,
                                  DateService dateService) {
        this.screeningRecordRepository = screeningRecordRepository;
        this.screeningRecordMapper = screeningRecordMapper;
        this.customerService = customerService;
        this.customerMapper = customerMapper;
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
        this.generateService = generateService;
        this.dateService = dateService;
    }

    public ScreeningRecordResponse createScreeningRecord(ScreeningRecordCreationRequest request) {
        Customer customer = customerService.findCustomerByIdentifierAndDob(
                request.getCustomerPhone(), request.getCustomerDob());
        Employee employee = employeeService.getEmployeeByUsername(request.getEmployeeUsername());
        ScreeningRecord screeningRecord = screeningRecordMapper.toScreeningRecord(request);
        String code = "";
        boolean isCodeUnique = false;
        while (!isCodeUnique) {
            code = "SR" + generateService.generateRandomNumber();
            // Kiểm tra xem mã đã tồn tại trong DB hay chưa
            isCodeUnique = !screeningRecordRepository.existsByScreeningRecordCode(code);
        }
        screeningRecord.setScreeningRecordCode(code);
        screeningRecord.setScreeningRecordDate(dateService.getDateNow());
        screeningRecord.setEmployee(employee);
        screeningRecord.setCustomer(customer);
        return screeningRecordMapper.toScreeningRecordResponse(
                screeningRecordRepository.save(screeningRecord));
    }

    public List<ScreeningRecordResponse> getAllScreeningRecordByCreateDate(LocalDate createDate) {
        List<ScreeningRecord> screeningRecordList = screeningRecordRepository.findAllByScreeningRecordDate(createDate);
        List<ScreeningRecordResponse> screeningRecordResponseList = new ArrayList<>();
        for (ScreeningRecord record : screeningRecordList) {
            ScreeningRecordResponse recordResponse = screeningRecordMapper.toScreeningRecordResponse(record);
            CustomerResponse customerResponse = customerMapper.toCustomerResponse(record.getCustomer());
            EmployeeResponse employeeResponse = employeeMapper.toEmployeeResponse(record.getEmployee());
            recordResponse.setCustomerResponse(customerResponse);
            recordResponse.setEmployeeResponse(employeeResponse);
            screeningRecordResponseList.add(recordResponse);
        }
        return screeningRecordResponseList;
    }
}
