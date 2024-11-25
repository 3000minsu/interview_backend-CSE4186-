package CSE4186.interview.repository;

import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfIntroductionRepository extends JpaRepository<SelfIntroduction,Long> {
    Page<SelfIntroduction> findAllByUser(Pageable pageable, User user);
}
