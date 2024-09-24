package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Customer;

import java.time.LocalDate;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByCustomerCode(String customerCode);

    Optional<Customer> findByCustomerCodeAndCustomerDob(String code,
                                                        LocalDate dob);

    Optional<Customer> findByCustomerPhoneAndCustomerDob(String phone,
                                                         LocalDate dob);
}
