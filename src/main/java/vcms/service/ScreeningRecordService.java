package vcms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcms.dto.request.ScreeningRecordCreationRequest;
import vcms.dto.response.ScreeningRecordResponse;
import vcms.mapper.CustomerMapper;
import vcms.mapper.EmployeeMapper;
import vcms.mapper.ScreeningRecordMapper;
import vcms.model.Customer;
import vcms.model.Employee;
import vcms.model.ScreeningRecord;
import vcms.repository.ScreeningRecordRepository;
import vcms.utils.DateService;

@Service
public class ScreeningRecordService {
    private final ScreeningRecordRepository screeningRecordRepository;

    private final ScreeningRecordMapper screeningRecordMapper;

    private final CustomerService customerService;

    private final CustomerMapper customerMapper;

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    @Autowired
    private DateService dateService;

    public ScreeningRecordService(ScreeningRecordRepository screeningRecordRepository,
                                  ScreeningRecordMapper screeningRecordMapper, CustomerService customerService,
                                  CustomerMapper customerMapper, EmployeeService employeeService,
                                  EmployeeMapper employeeMapper) {
        this.screeningRecordRepository = screeningRecordRepository;
        this.screeningRecordMapper = screeningRecordMapper;
        this.customerService = customerService;
        this.customerMapper = customerMapper;
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    public ScreeningRecordResponse createScreeningRecord(ScreeningRecordCreationRequest request) {
        Customer customer = customerService.findCustomerByIdentifierAndDob(
                request.getCustomerPhone(), request.getCustomerDob());
        Employee employee = employeeService.getEmployeeByUsername(request.getEmployeeUsername());
        ScreeningRecord screeningRecord = screeningRecordMapper.toScreeningRecord(request);
        screeningRecord.setScreeningRecordDate(dateService.getDateNow());
        screeningRecord.setEmployee(employee);
        screeningRecord.setCustomer(customer);
        return screeningRecordMapper.toScreeningRecordResponse(
                screeningRecordRepository.save(screeningRecord));
    }
}
