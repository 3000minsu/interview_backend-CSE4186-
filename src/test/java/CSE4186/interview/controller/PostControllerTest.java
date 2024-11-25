package CSE4186.interview.controller;

import CSE4186.interview.config.TestSecurityConfig;
import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.controller.dto.ReviewDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.JobField;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.service.CommentService;
import CSE4186.interview.service.PostService;
import CSE4186.interview.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

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
                .build();
    }

    private Post createAnotherPost() {
        return Post.builder()
                .id(2L)
                .title("테스트2")
                .content("내용 다름")
                .user(createUser())
                .jobField(createJobField())
                .build();
    }

    private PostDto.Response createPostResponse() {
        User user = createUser();
        return PostDto.Response
                .builder()
                .id(1L)
                .title("테스트")
                .content("내용")
                .createdAt(String.valueOf(LocalDateTime.now()))
                .updatedAt(String.valueOf(LocalDateTime.now()))
                .like(10)
                .dislike(0)
                .viewCount(100)
                .jobField(createJobField().getField())
                .userId(user.getId())
                .userName(user.getName())
                .build();
    }

    private PostDto.PostListResponse createPostListResponse() {
        List<PostDto.Response> postDtoList = new ArrayList<>();
        postDtoList.add(createPostResponse());
        return PostDto.PostListResponse.builder()
                .list(postDtoList)
                .pageCount(1)
                .build();
    }

    private Comment createComment() {
        return Comment.builder()
                .id(1L)
                .content("댓글")
                .post(createPost())
                .user(createUser())
                .build();
    }

    private CommentDto.Response createCommentResponse() {
        User user = createUser();
        return CommentDto.Response.builder()
                .id(1L)
                .content(new ReviewDto(commentJsonString))
                .username(user.getName())
                .userId(user.getId())
                .createdAt(String.valueOf(LocalDateTime.now()))
                .updatedAt(String.valueOf(LocalDateTime.now()))
                .build();
    }



    private final String commentJsonString = """
                        {
                            "verbal": [
                                5,
                                3,
                                5,
                                5,
                                5
                            ],
                            "nonverbal": [
                                4,
                                5,
                                4,
                                5
                            ],
                            "review": "1221"
                        }
                """;

    @Test
    void getAllPosts() throws Exception {
        given(postService.findPostsByCondition(Mockito.any(Pageable.class), Mockito.anyString(), Mockito.anyString())).willReturn(createPostListResponse());

        ResultActions actions = mvc.perform(get("/post/list")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body.pageCount").value(1));
    }

    @Test
    void getPost() throws Exception {
        given(postService.findPost(Mockito.anyLong())).willReturn(createPostResponse());

        ResultActions actions = mvc.perform(get("/post/1")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body.post.id").value(1L))
                .andExpect(jsonPath("$.body.post.title").value("테스트"))
                .andExpect(jsonPath("$.body.post.content").value("내용"));
    }

    @Test
    void addPost() throws Exception {
        PostDto.CreateRequest request = new PostDto.CreateRequest("title", "content", 1L, 1L, List.of(1L, 2L));

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(post("/post")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void updatePost() throws Exception {
        PostDto.UpdateRequest request = new PostDto.UpdateRequest("title", "content", 1L, List.of(1L, 2L));

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(put("/post/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void deletePost() throws Exception {
        ResultActions actions = mvc.perform(delete("/post/1")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("게시글이 삭제되었습니다."));
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto.CreateRequest request = new CommentDto.CreateRequest(commentJsonString, 1L);

        String content = objectMapper.writeValueAsString(request);
        ResultActions actions = mvc.perform(post("/post/1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void updateComment() throws Exception {
        CommentDto.UpdateRequest request = new CommentDto.UpdateRequest(1L, commentJsonString);

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(put("/post/1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void deleteComment() throws Exception {
        CommentDto.DeleteRequest request = new CommentDto.DeleteRequest(1L);

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(delete("/post/1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("댓글이 삭제되었습니다."));
    }
}