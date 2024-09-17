package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByCustomerCode(String customerCode);
}
