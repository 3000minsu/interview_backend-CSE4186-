package CSE4186.interview.entity;

import CSE4186.interview.controller.dto.SelfIntroductionDetailDto;
import CSE4186.interview.controller.dto.SelfIntroductionDto;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SelfIntroduction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "self_introduction_id")
    private Long id;

    private String title;

    @BatchSize(size = 5)
    @OneToMany(mappedBy = "selfIntroduction", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<SelfIntroductionDetail> selfIntroductionDetailList = new ArrayList<>();

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "selfIntroduction", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Question> quetions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public SelfIntroductionDto.Response toSelfIntroductionResponse() {
        return SelfIntroductionDto.Response.builder()
                .id(this.id)
                .title(this.title)
                .detailList(getSelfIntroductionDetailResponseList())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    private List<SelfIntroductionDetailDto.Response> getSelfIntroductionDetailResponseList() {
        return this.selfIntroductionDetailList.stream()
                .map(SelfIntroductionDetail::toSelfIntroductionDetailResponse)
                .toList();
    }


    public void changeTitle(String title) {
        this.title = title;
    }
}
