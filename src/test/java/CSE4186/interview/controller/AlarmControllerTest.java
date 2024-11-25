package CSE4186.interview.controller;

import CSE4186.interview.config.TestSecurityConfig;
import CSE4186.interview.controller.dto.AlarmDto;
import CSE4186.interview.service.AlarmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AlarmControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AlarmService alarmService;

    private AlarmDto createAlarmDto() {
        return AlarmDto.builder()
                .id(1L)
                .isRead(false)
                .content("test")
                .postId(1L)
                .build();
    }


    private List<AlarmDto> createAlarmDtoList() {
        AlarmDto alarmdto = createAlarmDto();
        ArrayList<AlarmDto> alarmList = new ArrayList<>();
        alarmList.add(alarmdto);
        return alarmList;
    }

    @Test
    void selectAllAlarms() throws Exception {
        given(alarmService.selectAllAlarms()).willReturn(createAlarmDtoList());

        ResultActions actions = mvc.perform(get("/alarm")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body[0].id").value(1))
                .andExpect(jsonPath("$.body[0].content").value("test"))
                .andExpect(jsonPath("$.body[0].isRead").value(false))
                .andExpect(jsonPath("$.body[0].postId").value(1));
    }

    @Test
    void getAlarm() throws Exception {
        given(alarmService.getAlarm(Mockito.anyLong())).willReturn(createAlarmDto());

        ResultActions actions = mvc.perform(get("/alarm/1")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body.id").value(1))
                .andExpect(jsonPath("$.body.content").value("test"))
                .andExpect(jsonPath("$.body.isRead").value(false))
                .andExpect(jsonPath("$.body.postId").value(1));
    }
}