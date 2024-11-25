package CSE4186.interview.controller;

import CSE4186.interview.config.TestSecurityConfig;
import CSE4186.interview.controller.dto.SelfIntroductionDetailDto;
import CSE4186.interview.controller.dto.SelfIntroductionDto;
import CSE4186.interview.service.SelfIntroductionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static CSE4186.interview.controller.dto.SelfIntroductionDto.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SelfIntroductionController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class SelfIntroductionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SelfIntroductionService selfIntroductionService;

    public SelfIntroductionDetailDto.Response createSelfIntroductionDetailResponse() {
        return SelfIntroductionDetailDto.Response.builder()
                .id(1L)
                .title("나의 성장과정을 설명하세요")
                .content("안녕하세요")
                .type("etc")
                .build();
    }

    public Response createSelfIntroductionResponse() {
        List<SelfIntroductionDetailDto.Response> detailList = new ArrayList<>();
        detailList.add(createSelfIntroductionDetailResponse());

        return Response.builder()
                .id(1L)
                .title("X기업 자기소개서")
                .detailList(detailList)
                .createdAt(String.valueOf(LocalDateTime.now()))
                .updatedAt(String.valueOf(LocalDateTime.now()))
                .build();
    }

    public SelfIntroductionListResponse createSelfIntroductionListResponse() {
        List<Response> selfIntroductionResponse = new ArrayList<>();
        selfIntroductionResponse.add(createSelfIntroductionResponse());
        return SelfIntroductionListResponse
                .builder()
                .list(selfIntroductionResponse)
                .pageCount(1)
                .build();
    }

    @Test
    void getSelfIntroductionList() throws Exception {
        given(selfIntroductionService.findAllSelfIntroductions(Mockito.any(Pageable.class), Mockito.anyLong())).willReturn(createSelfIntroductionListResponse());

        ResultActions actions = mvc.perform(get("/selfIntroduction/list")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body.pageCount").value(1));
    }

    @Test
    void createSelfIntroductionList() throws Exception {
        SelfIntroductionDetailRequest detailRequest = new SelfIntroductionDetailRequest("나의 미래를 서술하시오.", "테스트", "etc");
        List<SelfIntroductionDetailRequest> requestList = new ArrayList<>();
        requestList.add(detailRequest);
        CreateRequest req = new CreateRequest("제목", requestList);

        String content = objectMapper.writeValueAsString(req);

        ResultActions actions = mvc.perform(post("/selfIntroduction/save")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("자기소개서가 저장되었습니다."));
    }

    @Test
    void updateSelfIntroduction() throws Exception {
        SelfIntroductionDetailRequest detailRequest = new SelfIntroductionDetailRequest("나의 미래를 서술하시오.", "테스트", "etc");
        List<SelfIntroductionDetailRequest> requestList = new ArrayList<>();
        requestList.add(detailRequest);
        UpdateRequest req = new UpdateRequest("제목", requestList);

        String content = objectMapper.writeValueAsString(req);

        ResultActions actions = mvc.perform(put("/selfIntroduction/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("자기소개서가 수정되었습니다."));
    }

    @Test
    void deleteSelfIntroduction() throws Exception {
        ResultActions actions = mvc.perform(delete("/selfIntroduction/1")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("자기소개서가 삭제되었습니다."));
    }
}