package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.CustomerRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.CustomerResponse;
import vcms.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ApiResponse<List<CustomerResponse>> getAllCustomers() {
        return ApiResponse.<List<CustomerResponse>>builder()
                .result(customerService.getCustomers())
                .build();
    }

    @GetMapping("/detail/{customerId}")
    public ApiResponse<CustomerResponse> getCustomerById(
            @PathVariable("customerId") Long customerId) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getCustomer(customerId))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<CustomerResponse> createCustomer(
            @RequestBody CustomerRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.createCustomer(request))
                .build();
    }

    @PutMapping("/update/{customerId}")
    public ApiResponse<CustomerResponse> updateCustomerById(
            @PathVariable("customerId") Long customerId,
            @RequestBody CustomerRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.updateCustomer(customerId, request))
                .build();
    }

    @DeleteMapping("/delete/{customerId}")
    public ApiResponse<String> deleteCustomerById(
            @PathVariable("customerId") Long customerId) {
        customerService.deleteCustomer(customerId);
        return ApiResponse.<String>builder()
                .result("Customer has been deleted")
                .build();
    }
}
