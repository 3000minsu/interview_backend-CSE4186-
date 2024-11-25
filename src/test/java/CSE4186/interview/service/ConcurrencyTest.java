package CSE4186.interview.service;

import CSE4186.interview.controller.dto.LikesDto;
import CSE4186.interview.entity.Likes;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.login.Role;
import CSE4186.interview.repository.LikesRepository;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private LikesService likesService;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public User createUser() {
        return User.builder()
                .email("abc@gmail.com")
                .name("syh")
                .password("1234")
                .authority(Role.USER)
                .build();
    }

    public Post createPost() {
        return Post.builder()
                .title("test")
                .content("test")
                .dislikeCount(0)
                .likeCount(0)
                .viewCount(0)
                .build();
    }

    LikesDto.CreteRequest createLikeDto(Long id) {
        return new LikesDto.CreteRequest(id);
    }


    @Test
    @DisplayName("좋아요 누르기 동시성 테스트")
    void postLikeTest() throws InterruptedException {

        Post post = postRepository.save(createPost());
        int numberOfThreads = 20;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    User user = userRepository.save(createUser());
                    LikesDto.CreteRequest request = createLikeDto(user.getId());
                    likesService.addLike(request, post.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Post findPost = postRepository.findById(post.getId()).get();
        int cnt = likesRepository.countByPost(post);
        Assertions.assertThat(cnt).isEqualTo(20);
        Assertions.assertThat(findPost.getLikeCount()).isEqualTo(20);
    }
}
