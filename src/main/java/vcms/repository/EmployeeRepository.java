package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeUsername(String username);

    Optional<Employee> findByEmployeeEmail(String email);
}
