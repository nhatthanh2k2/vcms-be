package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
