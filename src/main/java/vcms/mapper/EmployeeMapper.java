package vcms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vcms.dto.request.EmployeeCreationRequest;
import vcms.dto.request.EmployeeUpdateRequest;
import vcms.dto.response.EmployeeResponse;
import vcms.model.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "roles", source = "roles")
    Employee toEmployee(EmployeeCreationRequest request);

    EmployeeResponse toEmployeeResponse(Employee employee);

    void updateEmployee(@MappingTarget Employee employee,
                        EmployeeUpdateRequest request);
}
