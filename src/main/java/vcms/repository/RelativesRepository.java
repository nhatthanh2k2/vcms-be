package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Customer;
import vcms.model.Relatives;

public interface RelativesRepository extends JpaRepository<Relatives, Long> {
    Relatives findByCustomer(Customer customer);
}
