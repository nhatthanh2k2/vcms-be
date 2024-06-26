package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.CustomerRequest;
import vcms.dto.response.CustomerResponse;
import vcms.mapper.CustomerMapper;
import vcms.model.Customer;
import vcms.repository.CustomerRepository;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final DateService dateService;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper,
                           DateService dateService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.dateService = dateService;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public CustomerResponse getCustomer(long id) {
        return customerMapper.toCustomerResponse(
                customerRepository.findById(id).orElseThrow(
                        () -> new RuntimeException("Customer Not Found")));
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
            Customer customer = customerMapper.toCustomer(request);
            LocalDateTime createDateTime = dateService.getDateTimeNow();
            customer.setCustomerCreateAt(createDateTime);
            customer.setCustomerUpdateAt(createDateTime);
            customerRepository.save(customer);
            LocalDate now = LocalDate.now();
            String strLocalDate = now.format(
                    DateTimeFormatter.ofPattern("ddMMyyyy"));
            String strCode =
                    "C" + strLocalDate + "-" + customer.getCustomerId();
            customer.setCustomerCode(strCode);
        return customerMapper.toCustomerResponse(
                customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(Long id,
                                                CustomerRequest request) {
            Customer customer =
                    customerRepository.findById(id).orElseThrow(
                            () -> new RuntimeException("Customer Not Found"));
            customerMapper.updateCustomer(customer, request);
            LocalDateTime updateDateTime = dateService.getDateTimeNow();
            customer.setCustomerUpdateAt(updateDateTime);
        return customerMapper.toCustomerResponse(
                customerRepository.save(customer));
    }

    public boolean deleteCustomer(Long id) {
        try {
            customerRepository.deleteById(id);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }
}
