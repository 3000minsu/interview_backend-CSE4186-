package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.SelfIntroductionDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class SelfIntroductionDetailDto {


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Schema(name = "selfIntroductionDetailResponse", description = "자소서 항목 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String type;
    }
}
