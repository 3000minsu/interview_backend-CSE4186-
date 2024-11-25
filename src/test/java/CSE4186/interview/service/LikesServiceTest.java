package CSE4186.interview.service;

import CSE4186.interview.controller.dto.LikesDto;
import CSE4186.interview.entity.*;
import CSE4186.interview.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LikesServiceTest {

    @InjectMocks
    private LikesService likesService;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private DislikeRepository dislikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private PostRepository postRepository;

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("서윤혁")
                .email("test@gmail.com")
                .password("password")
                .build();
    }

    private JobField createJobField() {
        return JobField.builder()
                .field("백엔드")
                .symbol("BE")
                .build();
    }

    private Post createPost() {
        return Post.builder()
                .id(1L)
                .title("테스트")
                .content("내용")
                .user(createUser())
                .jobField(createJobField())
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    @Test
    @DisplayName("좋아요 성공 테스트")
    void successLike() {
        User user = createUser();
        Post post = createPost();
        LikesDto.CreteRequest request = new LikesDto.CreteRequest(1L);

        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(user));
        given(postRepository.findPostForUpdateById(Mockito.anyLong())).willReturn(Optional.of(post));
        given(likesRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.empty());
        given(dislikeRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.empty());

        String result = likesService.addLike(request, 1L);
        assertThat(result).isEqualTo("좋아요 성공");
    }

    @Test
    @DisplayName("좋아요 실패 테스트 (싫어요 누름)")
    void failLikeByDisLike() {
        User user = createUser();
        Post post = createPost();
        Dislike dislike = new Dislike(1L, user, post);
        LikesDto.CreteRequest request = new LikesDto.CreteRequest(1L);

        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(user));
        given(postRepository.findPostForUpdateById(Mockito.anyLong())).willReturn(Optional.of(post));
        given(likesRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.empty());
        given(dislikeRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.of(dislike));

        assertThrows(IllegalStateException.class, () -> likesService.addLike(request, 1L));
    }

    @Test
    @DisplayName("좋아요 실패 테스트")
    void failLike() {
        User user = createUser();
        Post post = createPost();
        Likes like = new Likes(1L, user, post);
        LikesDto.CreteRequest request = new LikesDto.CreteRequest(1L);

        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(user));
        given(postRepository.findPostForUpdateById(Mockito.anyLong())).willReturn(Optional.of(post));
        given(likesRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.of(like));

        String result = likesService.addLike(request, 1L);
        assertThat(result).isEqualTo("좋아요 취소");
    }
}