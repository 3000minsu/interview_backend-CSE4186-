package CSE4186.interview.entity;

import CSE4186.interview.controller.dto.SelfIntroductionDetailDto;
import CSE4186.interview.controller.dto.SelfIntroductionDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfIntroductionDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String type;
    //기술항목,인성항목,기타

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "self_introduction_id")
    private SelfIntroduction selfIntroduction;

    public SelfIntroductionDetailDto.Response toSelfIntroductionDetailResponse() {
        return SelfIntroductionDetailDto.Response.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .type(this.type)
                .build();
    }
}
