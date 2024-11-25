package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.JobField;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Schema(name = "postCreateRequest", description = "게시글 생성 DTO")
    public static class CreateRequest {
        @NotNull(message = "게시글 제목을 입력해주세요")
        private String title;
        @NotNull(message = "게시글 내용을 입력해주세요")
        private String content;

        @NotNull
        private Long userId;
        @NotNull
        private Long jobFieldId;
        @NotNull
        private List<Long> videoIdList;

        public Post toEntity(User user, JobField jobField) {
            return Post.builder()
                    .title(this.title)
                    .content(this.content)
                    .dislikeCount(0)
                    .likeCount(0)
                    .viewCount(0)
                    .user(user)
                    .jobField(jobField)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @Schema(name = "postUpdateRequest", description = "게시글 수정 DTO")
    public static class UpdateRequest {

        @NotNull
        private String title;
        @NotNull
        private String content;
        @NotNull
        private Long jobFieldId;
        @NotNull
        private List<Long> videoIdList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Schema(name = "postResponse", description = "게시글 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private Integer like;
        private Integer dislike;
        private Integer viewCount;
        private String createdAt;
        private String updatedAt;
        private String jobField;
        private Long userId;
        private String userName;
        private List<CommentDto.Response> comments;
        private List<VideoDto.Response> videoList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Schema(name = "postDetailResponse", description = "게시글 응답 detail DTO")
    public static class PostDetailResponse {
        private Response post;
        private String checkLikeOrDislike;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Schema(name = "postListResponse", description = "게시글 전체 list 응답 DTO")
    public static class PostListResponse {
        @NotNull
        private List<PostDto.Response> list;
        private int pageCount;
    }
}
