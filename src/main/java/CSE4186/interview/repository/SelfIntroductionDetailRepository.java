package CSE4186.interview.repository;

import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.SelfIntroductionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfIntroductionDetailRepository extends JpaRepository<SelfIntroductionDetail, Long> {

    void deleteBySelfIntroduction(SelfIntroduction selfIntroduction);
}
