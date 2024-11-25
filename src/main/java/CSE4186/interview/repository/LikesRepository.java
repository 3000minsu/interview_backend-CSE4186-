package CSE4186.interview.repository;

import CSE4186.interview.entity.Likes;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

     @Query("select l from Likes l join l.user u join l.post p where u.id = :userId and p.id = :postId")
     Optional<Likes> findAllByPostAndUser(Long postId, Long userId);

     int countByPost(Post post);
}
