package CSE4186.interview.repository;

import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findAllByUser(Pageable pageable, User user);
}
