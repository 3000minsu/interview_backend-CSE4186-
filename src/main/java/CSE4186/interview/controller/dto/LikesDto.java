package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Likes;
import CSE4186.interview.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LikesDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "likeCreateRequest", description = "좋아요 DTO")
    public static class CreteRequest {
        @NotNull private Long userId;
    }
}
