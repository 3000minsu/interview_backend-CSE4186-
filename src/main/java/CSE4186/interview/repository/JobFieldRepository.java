package CSE4186.interview.repository;

import CSE4186.interview.entity.JobField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobFieldRepository extends JpaRepository<JobField, Long> {
}
