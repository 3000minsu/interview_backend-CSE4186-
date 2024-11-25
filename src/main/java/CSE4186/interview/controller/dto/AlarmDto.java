package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Alarm;
import CSE4186.interview.entity.Post;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class AlarmDto {
    private Long id;
    private String content;
    private Boolean isRead;
    private Long postId;

    public AlarmDto(Alarm alarm) {
        this.id = alarm.getId();
        this.content = alarm.getContent();
        this.isRead = alarm.getIsRead();
        this.postId = alarm.getPost().getId();
    }

    @Builder
    public AlarmDto(Long id, String content, Boolean isRead, Long postId) {
        this.id = id;
        this.content = content;
        this.isRead = isRead;
        this.postId = postId;
    }
}
