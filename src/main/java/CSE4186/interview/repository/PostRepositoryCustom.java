package CSE4186.interview.repository;

import CSE4186.interview.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<Post> findPostsBySearchCondition(String q, String condition, Pageable pageable);
}
