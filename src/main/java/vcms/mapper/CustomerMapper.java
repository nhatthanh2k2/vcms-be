package vcms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vcms.dto.request.CustomerRequest;
import vcms.dto.response.CustomerResponse;
import vcms.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toCustomer(CustomerRequest request);

    CustomerResponse toCustomerResponse(Customer customer);

    void updateCustomer(@MappingTarget Customer customer,
                        CustomerRequest request);
}
