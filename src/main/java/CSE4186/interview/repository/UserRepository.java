package CSE4186.interview.repository;

import CSE4186.interview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    @Query("SELECT CASE" +
            " WHEN (SELECT COUNT(l) FROM Likes l WHERE l.user.id = :userId and l.post.id = :postId) > 0 THEN 'like' " +
            " WHEN (SELECT COUNT(d) FROM Dislike d WHERE d.user.id = :userId and d.post.id = :postId) > 0 THEN 'dislike' " +
            " ELSE 'none' END ")
    String findUserByLikesAndDislike(Long postId, Long userId);
}
