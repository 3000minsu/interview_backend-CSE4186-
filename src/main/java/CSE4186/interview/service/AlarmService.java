package CSE4186.interview.service;

import CSE4186.interview.controller.dto.AlarmDto;
import CSE4186.interview.entity.Alarm;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional(readOnly = true)
    public List<AlarmDto> selectAllAlarms() {
        List<Alarm> alarmList = alarmRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return alarmList.stream().map(AlarmDto::new).toList();
    }

    public AlarmDto getAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new NotFoundException("해당 알림을 찾을 수 없습니다."));
        alarm.readAlarm();
        return new AlarmDto(alarm);
    }
}
