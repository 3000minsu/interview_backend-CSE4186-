package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class VideoDto {

    @Data
    @AllArgsConstructor
    @Schema(name = "videoCreateRequest", description = "비디오 생성 DTO")
    public static class CreateRequest {
        @NotNull(message = "비디오 제목을 입력해주세요")
        private String title;
        @NotNull(message = "비디오 링크를 입력해주세요")
        private String link;
        private Long userId;

        public Video toEntity(User user) {
            return Video.builder()
                    .title(this.title)
                    .link(this.link)
                    .user(user)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Schema(name = "videoResponse", description = "비디오 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String link;
        private Long userId;
        private String createdAt;
        private String updatedAt;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Schema(name = "videoListResponse", description = "비디오 전체 list 응답 DTO")
    public static class VideoListResponse {
        private List<Response> list;
        private int pageCount;
    }
}
