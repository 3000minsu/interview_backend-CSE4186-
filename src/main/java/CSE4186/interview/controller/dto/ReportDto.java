package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Report;
import CSE4186.interview.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ReportDto {

    @Data
    @AllArgsConstructor
    @Schema(name = "reportCreateRequest", description = "신고 생성 DTO")
    public static class CreateRequest {
        @NotNull
        private String reportType;
        @NotNull
        private Long userId;
        @NotNull
        private Long targetId;
    }
}
