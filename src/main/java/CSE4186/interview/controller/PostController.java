package CSE4186.interview.controller;

import CSE4186.interview.annotation.LoginUser;
import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.service.CommentService;
import CSE4186.interview.service.PostService;
import CSE4186.interview.service.UserService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post", description = "Post API")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/list")
    @Operation(summary = "Get All Posts", description = "모든 게시글을 조회",
            parameters = {
                    @Parameter(
                            name = "q",
                            description = "검색어",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "직무 여러개 검색 -> BE,FE,AP")
                    ),
                    @Parameter(
                            name = "searchBy",
                            description = "검색 설정",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "username, title, field")
                    ),
            }
    )
    public ApiUtil.ApiSuccessResult<PostDto.PostListResponse> getAllPosts(
            @PageableDefault(page = 1, size = 10) Pageable pageable,
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "searchBy", defaultValue = "") String searchBy
    ) {
        return ApiUtil.success(postService.findPostsByCondition(pageable, q, searchBy));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get Post", description = "게시글 상세")
    public ApiUtil.ApiSuccessResult<PostDto.PostDetailResponse> getPost(
            @PathVariable(name = "id") Long id,
            @LoginUser User loginUser
    ) {
        Long userId = Long.valueOf(loginUser.getUsername());
        PostDto.Response postDto = postService.findPost(id);
        String checkLikeOrDislike = userService.checkLikeOrDislike(id, userId);

        return ApiUtil.success(new PostDto.PostDetailResponse(postDto, checkLikeOrDislike));
    }

    @PostMapping
    @Operation(summary = "Add Post", description = "게시글 생성")
    public ApiUtil.ApiSuccessResult<String> createPost(@Valid @RequestBody PostDto.CreateRequest request) {
        postService.addPost(request);
        return ApiUtil.success("게시글이 생성되었습니다.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Post", description = "게시글 수정")
    public ApiUtil.ApiSuccessResult<String> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto.UpdateRequest request) {
        postService.updatePost(id, request);
        return ApiUtil.success("게시글이 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Post", description = "게시글 삭제")
    public ApiUtil.ApiSuccessResult<String> deletePost(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        return ApiUtil.success("게시글이 삭제되었습니다.");
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Add Comment", description = "댓글 생성")
    public ApiUtil.ApiSuccessResult<String> addComment(@Valid @RequestBody CommentDto.CreateRequest request,
                                                                 @PathVariable(name = "id") Long id) {
        commentService.addComment(request, id);
        return ApiUtil.success("댓글이 생성되었습니다.");
    }

    @PutMapping("/{id}/comment")
    @Operation(summary = "Update Comment", description = "댓글 수정")
    public ApiUtil.ApiSuccessResult<String> updateComment(@Valid @RequestBody CommentDto.UpdateRequest request) {
        commentService.updateComment(request);
        return ApiUtil.success("댓글이 수정되었습니다.");
    }

    @DeleteMapping("/{id}/comment")
    @Operation(summary = "Delete Comment", description = "댓글 삭제")
    public ApiUtil.ApiSuccessResult<String> deleteComment(@Valid @RequestBody CommentDto.DeleteRequest request) {
        commentService.deleteComment(request.getId());
        return ApiUtil.success("댓글이 삭제되었습니다.");
    }
}
