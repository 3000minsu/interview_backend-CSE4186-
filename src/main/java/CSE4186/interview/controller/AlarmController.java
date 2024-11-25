package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.AlarmDto;
import CSE4186.interview.service.AlarmService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
@Tag(name = "Alarm", description = "Alarm API")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public ApiUtil.ApiSuccessResult<List<AlarmDto>> selectAllAlarms() {
        return ApiUtil.success(alarmService.selectAllAlarms());
    }

    @GetMapping("/{id}")
    public ApiUtil.ApiSuccessResult<AlarmDto> getAlarm(
            @PathVariable("id") Long alarmId
    ) {
        return ApiUtil.success(alarmService.getAlarm(alarmId));
    }
}
