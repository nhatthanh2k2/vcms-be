package vcms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vcms.dto.request.CustomerRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.CustomerResponse;
import vcms.model.Customer;
import vcms.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ApiResponse<List<Customer>> getAllCustomers() {
        ApiResponse<List<Customer>> apiResponse = new ApiResponse<>();
        try {
            apiResponse.setResult(customerService.getCustomers());
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<CustomerResponse> getCustomerById(
            @PathVariable("id") Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(customerService.getCustomer(id));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @PostMapping("/create")
    public ApiResponse<CustomerResponse> createCustomer(
            @RequestBody CustomerRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(customerService.createCustomer(request));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<CustomerResponse> updateCustomerById(
            @PathVariable("id") Long id,
                                                    @RequestBody CustomerRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setResult(customerService.updateCustomer(id, request));
            apiResponse.setSuccess(true);
        }
        catch (Exception exception) {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
        }
        return apiResponse;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteCustomerById(@PathVariable("id") Long id) {
        ApiResponse apiResponse = new ApiResponse();
        if (customerService.deleteCustomer(id)) {
            apiResponse.setMessage("Customer deleted successfully");
            apiResponse.setSuccess(true);
        }
        else {
            apiResponse.setSuccess(false);
            apiResponse.setCode(9999);
            apiResponse.setMessage("Customer deleted failed");
        }
        return apiResponse;
    }
}
