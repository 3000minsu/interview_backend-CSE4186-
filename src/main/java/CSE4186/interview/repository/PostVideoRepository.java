package CSE4186.interview.repository;

import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.PostVideo;
import CSE4186.interview.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostVideoRepository extends JpaRepository<PostVideo, Long> {

     Boolean existsByVideo(Video video);
    void deleteByPost(Post post);

    void deleteByVideo(Video video);
}
