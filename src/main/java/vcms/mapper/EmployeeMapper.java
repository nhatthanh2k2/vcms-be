package vcms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.response.EmployeeResponse;
import vcms.model.Employee;
import vcms.repository.EmployeeRepository;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Employee toEmployee(EmployeeCreationRequest request);

    EmployeeResponse toEmployeeResponse(Employee employee);

    void updateEmployee(@MappingTarget Employee employee,
                        EmployeeUpdateRequest request);
}
