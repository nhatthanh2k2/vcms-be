package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.CustomerRequest;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.response.CustomerResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.CustomerMapper;
import vcms.model.Customer;
import vcms.repository.CustomerRepository;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse).toList();
    }

    public CustomerResponse getCustomer(long customerId) {
        return customerMapper.toCustomerResponse(
                customerRepository.findById(customerId).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_EXISTED)));
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

    public CustomerResponse updateCustomer(Long customerId,
                                                CustomerRequest request) {
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

    private boolean isValidVietnamPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^0[3-9]\\d{8}$");
    }

    public CustomerResponse lookupCustomer(LookupCustomerRequest request) {
        boolean isPhoneNumber = isValidVietnamPhoneNumber(
                request.getLookupCustomerCode());
        Optional<Customer> optionalCustomer;
        if (isPhoneNumber) {
            optionalCustomer = customerRepository.findByCustomerPhoneAndCustomerDob(
                    request.getLookupCustomerCode(),
                    request.getLookupCustomerDob());
        }
        else {
            optionalCustomer = customerRepository.findByCustomerCodeAndCustomerDob(
                    request.getLookupCustomerCode(),
                    request.getLookupCustomerDob());
        }
        if (optionalCustomer.isPresent())
            return customerMapper.toCustomerResponse(optionalCustomer.get());
        return new CustomerResponse();
    }

}
