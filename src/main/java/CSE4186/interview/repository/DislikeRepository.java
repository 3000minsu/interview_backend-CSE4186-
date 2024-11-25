package CSE4186.interview.repository;

import CSE4186.interview.entity.Dislike;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DislikeRepository extends JpaRepository<Dislike, Long> {

    @Query("select l from Dislike l join l.user u join l.post p where u.id = :userId and p.id = :postId")
    Optional<Dislike> findAllByPostAndUser(Long postId, Long userId);
}
