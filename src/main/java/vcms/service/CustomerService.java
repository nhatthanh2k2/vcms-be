package vcms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcms.dto.request.CustomerRequest;
import vcms.dto.response.ApiResponse;

import vcms.mapper.CustomerMapper;
import vcms.model.Customer;
import vcms.repository.CustomerRepository;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DateService dateService;

    public ApiResponse<List<Customer>> getCustomers() {

        ApiResponse<List<Customer>> apiResponse = new ApiResponse<>();
        try {
            List<Customer> customers = customerRepository.findAll();
            apiResponse.setResult(customers);
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(new ArrayList<>());
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<Customer> getCustomer(long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Customer customer =
                    customerRepository.findById(id).orElseThrow(
                            () -> new RuntimeException("Customer Not Found"));
            apiResponse.setResult(customer);
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<Customer> createCustomer(CustomerRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
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
            apiResponse.setResult(customerMapper.toCustomerResponse(customerRepository.save(customer)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<Customer> updateCustomer(Long id,
                                                CustomerRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Customer customer =
                    customerRepository.findById(id).orElseThrow(
                            () -> new RuntimeException("Customer Not Found"));
            customerMapper.updateCustomer(customer, request);
            LocalDateTime updateDateTime = dateService.getDateTimeNow();
            customer.setCustomerUpdateAt(updateDateTime);
            apiResponse.setResult(customerMapper.toCustomerResponse(customerRepository.save(customer)));
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult(null);
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse<String> deleteCustomer(Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            customerRepository.deleteById(id);
            apiResponse.setResult("Customer deleted successfully");
            apiResponse.setSuccess(true);
        }
        catch (Exception ex) {
            apiResponse.setResult("Customer deleted failed");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }
}
