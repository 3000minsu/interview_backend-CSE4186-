package CSE4186.interview.service;

import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.JobField;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

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
                .id(1L)
                .field("백엔드")
                .symbol("백엔드")
                .build();
    }

    private Pageable createPageable() {
        return PageRequest.of(0, 10);
    }

    private Post createPost() {
        return Post.builder()
                .id(1L)
                .title("테스트")
                .content("내용")
                .user(createUser())
                .jobField(createJobField())
                .comments(new ArrayList<>())
                .postVideo(new ArrayList<>())
                .build();
    }

    private Post createAnotherPost() {
        return Post.builder()
                .id(2L)
                .title("테스트2")
                .content("내용 다름")
                .user(createUser())
                .comments(new ArrayList<>())
                .postVideo(new ArrayList<>())
                .jobField(createJobField())
                .build();
    }

    private Page<Post> createPagePost() {
        List<Post> postList = new ArrayList<>();
        postList.add(createPost());
        postList.add(createAnotherPost());
        return new PageImpl<>(postList, createPageable(), 2);
    }

    private Comment createComment() {
        return Comment.builder()
                .id(1L)
                .content("댓글")
                .post(createPost())
                .user(createUser())
                .build();
    }

    private List<Comment> createCommentList() {
        List<Comment> commentList = new ArrayList<>();
        commentList.add(createComment());
        return commentList;
    }

    @Test
    @DisplayName("조건으로 검색")
    void findPostsByCondition() {
        Page<Post> postPage = createPagePost();

        given(postRepository.findPostsBySearchCondition(Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .willReturn(postPage);

        PostDto.PostListResponse result = postService.findPostsByCondition(PageRequest.of(1, 10), "test", "title");

        List<PostDto.Response> list = result.getList();

        assertThat(result.getPageCount()).isEqualTo(1);
        assertThat(list.get(0).getTitle()).isEqualTo("테스트");
        assertThat(list.get(1).getTitle()).isEqualTo("테스트2");
    }

    @Test
    @DisplayName("전체 검색")
    void findAll() {
        Page<Post> postPage = createPagePost();

        given(postRepository.findPostsBySearchCondition(Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .willReturn(postPage);

        PostDto.PostListResponse result = postService.findPostsByCondition(PageRequest.of(1, 10), "", "");

        List<PostDto.Response> list = result.getList();

        assertThat(result.getPageCount()).isEqualTo(1);
        assertThat(list.get(0).getTitle()).isEqualTo("테스트");
        assertThat(list.get(1).getTitle()).isEqualTo("테스트2");
    }
}