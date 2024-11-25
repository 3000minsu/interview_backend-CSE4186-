package CSE4186.interview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DislikeDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "dislikeCreateRequest", description = "싫어요 DTO")
    public static class CreteRequest {
        @NotNull
        private Long userId;
    }
}
