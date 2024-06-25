package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmployeeUsername(String username);
}
