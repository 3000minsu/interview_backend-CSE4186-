package CSE4186.interview.entity;

import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.ReviewDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public CommentDto.Response toCommentResponse() {
        return CommentDto.Response
                .builder()
                .id(this.id)
                .content(new ReviewDto(this.content))
                .username(user.getName())
                .userId(user.getId())
                .createdAt(String.valueOf(this.getCreatedAt()))
                .updatedAt(String.valueOf(this.getUpdatedAt()))
                .build();
    }


    public void updateComment(String content) {
        this.content = content;
    }
}
