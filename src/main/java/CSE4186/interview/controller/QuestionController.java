package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.QuestionDto;
import CSE4186.interview.controller.dto.SelfIntroductionDto;
import CSE4186.interview.service.QuestionService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
//컨트롤러에 tag
@RestController
@RequiredArgsConstructor
@Tag(name = "Question", description = "Question API")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/question/create")
    @Operation(summary = "Create Question", description = "Gemini를 사용하여 질문 생성")
    public ApiUtil.ApiSuccessResult <Map<String, List<List<Map<String,String>>>>> createQuestionWithGemini(@RequestBody QuestionDto.Request request) {

        int questionNum= request.getQuestionNum();
        int selfIntroductionId = request.getSelfIntroductionId();
        int deptNum= request.getDeptNum();
        List<String> additionalQuestions = request.getAdditionalQuestions();
        return ApiUtil.success(questionService.createQuestion(questionNum,deptNum,selfIntroductionId, additionalQuestions));
    }

    @PostMapping("/question/followUp")
    @Operation(summary = "Create Follow-Up Question",description = "꼬리 질문 생성")
    public ApiUtil.ApiSuccessResult <Map<String,Object>> createFollowUpQuestionWithGemini(@RequestBody QuestionDto.followUpRequest request){
        int turn= request.getTurn();
        int selfIntroductionId= request.getSelfIntroductionId();
        int deptNum=request.getDeptNum();
        List<Map<String,String>> prevChats=request.getQuestions();
        String userAudio = request.getUserAudio();
        return ApiUtil.success(questionService.createFollowUpQuestion(turn,deptNum,selfIntroductionId,prevChats, userAudio));
    }

}
