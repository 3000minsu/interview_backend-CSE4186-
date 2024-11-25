package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class CommentDto {

    @Data
    @AllArgsConstructor
    @Schema(name = "commentCreateRequest", description = "댓글 생성 DTO")
    public static class CreateRequest {
        @NotNull private String content;
        @NotNull private Long userId;

        public Comment toEntity(User user, Post post) {
            return Comment.builder()
                    .content(this.content)
                    .user(user)
                    .post(post)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @Schema(name = "commentUpdateRequest", description = "댓글 수정 DTO")
    public static class UpdateRequest {
        @NotNull
        private Long id;
        @NotNull
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "commentDeleteRequest", description = "댓글 삭제 DTO")
    public static class DeleteRequest {
        @NotNull
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Schema(name = "commentResponse", description = "댓글 응답 DTO")
    public static class Response {
        private Long id;
        private ReviewDto content;
        private String username;
        private String createdAt;
        private String updatedAt;
        private Long userId;
    }
}
