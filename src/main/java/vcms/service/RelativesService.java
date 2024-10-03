package vcms.service;

import org.springframework.stereotype.Service;
import vcms.model.Customer;
import vcms.model.Relatives;
import vcms.repository.RelativesRepository;

@Service
public class RelativesService {
    private final RelativesRepository relativesRepository;

    public RelativesService(RelativesRepository relativesRepository) {
        this.relativesRepository = relativesRepository;
    }

    public Relatives getRelativesByCustomer(Customer customer) {
        return relativesRepository.findByCustomer(customer);
    }

    public void addRelatives(Relatives relatives) {
        relativesRepository.save(relatives);
    }
}
