package CSE4186.interview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


public class QuestionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "questionCreateRequest", description = "질문 생성 DTO")
    public static class Request{
        private int questionNum;
        private int selfIntroductionId;
        private int deptNum;
        private List<String> additionalQuestions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name="followUpQuestionCreateRequest",description = "꼬리 질문 생성 DTO")
    public static class followUpRequest{
        private int turn;
        private int selfIntroductionId;
        private int deptNum;
        private List<Map<String,String>> questions;
        private String userAudio;
    }
}
