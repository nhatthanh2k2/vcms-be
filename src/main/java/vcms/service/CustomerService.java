package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.CustomerRequest;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.response.CustomerResponse;
import vcms.enums.Gender;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.CustomerMapper;
import vcms.model.Customer;
import vcms.model.Relatives;
import vcms.repository.CustomerRepository;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final DateService dateService;

    private final RelativesService relativesService;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper,
                           DateService dateService, RelativesService relativesService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.dateService = dateService;
        this.relativesService = relativesService;
    }

    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse).toList();
    }

    public CustomerResponse getCustomer(long customerId) {
        return customerMapper.toCustomerResponse(
                customerRepository.findById(customerId).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_EXISTED)));
    }

    public Customer getCustomerByCustomerCode(String code) {
        return customerRepository.findByCustomerCode(code);
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = customerMapper.toCustomer(request);
        LocalDateTime createDateTime = dateService.getDateTimeNow();
        customer.setCustomerCreateAt(createDateTime);
        customer.setCustomerUpdateAt(createDateTime);
        Relatives relatives = new Relatives();
        relatives.setRelativesFullName(request.getRelativesFullName());
        relatives.setRelativesPhone(request.getRelativesPhone());
        relatives.setRelativesRelationship(request.getRelativesRelationship());

        relativesService.addRelatives(relatives);
        customerRepository.save(customer);
        relatives.setCustomer(customer);
        customer.setRelatives(relatives);
        relativesService.addRelatives(relatives);
        customerRepository.save(customer);
        LocalDate now = LocalDate.now();
        String strLocalDate = now.format(
                DateTimeFormatter.ofPattern("ddMMyyyy"));
        String strCode =
                "C" + strLocalDate + "-" + customer.getCustomerId();
        customer.setCustomerCode(strCode);
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new AppException(ErrorCode.NOT_EXISTED));
        customerMapper.updateCustomer(customer, request);
        LocalDateTime updateDateTime = dateService.getDateTimeNow();
        customer.setCustomerUpdateAt(updateDateTime);
        return customerMapper.toCustomerResponse(
                customerRepository.save(customer));
    }

    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public Customer findCustomerByIdentifierAndDob(String customerIdentifier, LocalDate dob) {
        Optional<Customer> optionalCustomer = Optional.empty();

        if (customerIdentifier.startsWith("0")) {
            optionalCustomer = customerRepository.findByCustomerPhoneAndCustomerDob(
                    customerIdentifier, dob);
        }
        else if (customerIdentifier.startsWith("C")) {
            optionalCustomer = customerRepository.findByCustomerCodeAndCustomerDob(
                    customerIdentifier, dob);
        }
        if (optionalCustomer.isPresent()) {
            return optionalCustomer.get();
        }
        throw new AppException(ErrorCode.NOT_EXISTED);
    }

    public CustomerResponse lookupCustomer(LookupCustomerRequest request) {

        Optional<Customer> optionalCustomer = Optional.empty();
        if (request.getCustomerIdentifier().startsWith("0")) {
            optionalCustomer = customerRepository.findByCustomerPhoneAndCustomerDob(
                    request.getCustomerIdentifier(),
                    request.getCustomerDob());
        }
        else if (request.getCustomerIdentifier().startsWith("C")) {
            optionalCustomer = customerRepository.findByCustomerCodeAndCustomerDob(
                    request.getCustomerIdentifier(),
                    request.getCustomerDob());
        }
        if (optionalCustomer.isPresent())
            return customerMapper.toCustomerResponse(optionalCustomer.get());
        throw new AppException(ErrorCode.NOT_EXISTED);
    }

    public void insertInitialCustomerData() {
        List<CustomerRequest> customerRequestList = new ArrayList<>();

        CustomerRequest customer1 = new CustomerRequest(
                "Đỗ Nhật Thanh", Gender.MALE, LocalDate.of(2002, 8, 19),
                "dothanhst2002@gmail.com", "0391234567",
                "Thành phố Cần Thơ", "Quận Ninh Kiều", "Phường Xuân Khánh",
                "Nguyễn Văn An", "0393683686", "Họ hàng"
        );

        CustomerRequest customer2 = new CustomerRequest(
                "Nguyễn Thị Bích", Gender.FEMALE, LocalDate.of(1995, 10, 10),
                "bichnguyen1995@gmail.com", "0376543210",
                "Thành phố Cần Thơ", "Quận Bình Thủy", "Phường Long Hòa",
                "Nguyễn Văn Tâm", "0356789123", "Anh"
        );

        CustomerRequest customer3 = new CustomerRequest(
                "Trần Văn Hải", Gender.MALE, LocalDate.of(1988, 5, 15),
                "tranvanhai88@gmail.com", "0389876543",
                "Thành phố Cần Thơ", "Quận Cái Răng", "Phường Ba Láng",
                "Trần Văn Phú", "0385432176", "Anh"
        );

        CustomerRequest customer4 = new CustomerRequest(
                "Lê Thị Hồng", Gender.FEMALE, LocalDate.of(1990, 3, 20),
                "lethihong90@gmail.com", "0364321987",
                "Tỉnh Hậu Giang", "Thành phố Vị Thanh", "Phường 4",
                "Lê Văn Minh", "0354321987", "Em"
        );


        CustomerRequest customer5 = new CustomerRequest(
                "Phạm Văn Hùng", Gender.MALE, LocalDate.of(1992, 12, 24),
                "phamvanhung92@gmail.com", "0343219876",
                "Tỉnh An Giang", "Thành phố Long Xuyên", "Phường Mỹ Xuyên",
                "Phạm Thị Lan", "0346547890", "Chị"
        );

        customerRequestList.add(customer1);
        customerRequestList.add(customer2);
        customerRequestList.add(customer3);
        customerRequestList.add(customer4);
        customerRequestList.add(customer5);
        try {
            for (CustomerRequest request : customerRequestList) {
                createCustomer(request);
            }
            System.out.println("Customer Data Inserted Successfully!");
        }
        catch (Exception exception) {
            System.out.println("Customer Data Inserted Failed!");
        }

    }

}
