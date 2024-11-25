package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.SelfIntroductionDetail;
import CSE4186.interview.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class SelfIntroductionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionCreateRequest", description = "자소서 생성 DTO")
    public static class CreateRequest{
        @NotNull private String title;
        @NotNull private List<SelfIntroductionDetailRequest> detailList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionUpdateRequest", description = "자소서 수정 DTO")
    public static class UpdateRequest{
        @NotNull private String title;
        @NotNull private List<SelfIntroductionDetailRequest> detailList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionDetailRequest", description = "자소서 detail DTO")
    public static class SelfIntroductionDetailRequest{
        @NotNull(message = "자기소개서 제목을 입력해주세요.")
        private String title;
        @NotNull(message = "자기소개서 내용을 입력해주세요.")
        private String content;
        @NotNull(message = "자기소개서 타입을 명시해주세요.")
        private String type;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Schema(name = "selfIntroductionRequest", description = "자소서 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String createdAt;
        private String updatedAt;
        private List<SelfIntroductionDetailDto.Response> detailList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Schema(name = "selfIntroductionListResponse", description = "자소서 전체 list 응답 DTO")
    public static class SelfIntroductionListResponse {
        @NotNull
        private List<SelfIntroductionDto.Response> list;
        private int pageCount;
    }


}
