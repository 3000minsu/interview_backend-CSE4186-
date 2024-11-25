package CSE4186.interview.repository;

import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;



    @Test
    @Transactional
    @DisplayName("N+1 문제 테스트")
    void queryTest() {
        System.out.println("==START==");
        List<Post> posts = postRepository.findAll();
        System.out.println("==END==");

        for(Post p : posts) {
            System.out.println(p.getComments().size());
            System.out.println(p.getPostVideo().size());
        }
    }
}