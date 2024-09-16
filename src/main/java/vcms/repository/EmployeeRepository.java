package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Employee;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeUsername(String username);

    Optional<Employee> findByEmployeeEmail(String email);

    List<Employee> findByRolesIn(Set<String> roles);
}
