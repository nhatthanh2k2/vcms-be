package vcms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vcms.dto.request.CustomerRequest;
import vcms.dto.response.ApiResponse;
import vcms.model.Customer;
import vcms.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ApiResponse<List<Customer>> getAllCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<Customer> getCustomerById(@PathVariable("id") Long id) {
        return customerService.getCustomer(id);
    }

    @PostMapping("/create")
    public ApiResponse<Customer> createCustomer(@RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @PutMapping("/update/{id}")
    public ApiResponse<Customer> updateCustomerById(@PathVariable("id") Long id,
                                                    @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteCustomerById(@PathVariable("id") Long id) {
        return customerService.deleteCustomer(id);
    }
}
