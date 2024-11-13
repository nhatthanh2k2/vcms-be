package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.News;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByNewsStatus(String status);
}