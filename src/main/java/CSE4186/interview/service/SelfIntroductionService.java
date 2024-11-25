package CSE4186.interview.service;

import CSE4186.interview.controller.dto.SelfIntroductionDto;
import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.SelfIntroductionDetail;
import CSE4186.interview.entity.User;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.SelfIntroductionDetailRepository;
import CSE4186.interview.repository.SelfIntroductionRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelfIntroductionService {

    private final SelfIntroductionRepository selfIntroductionRepository;
    private final SelfIntroductionDetailRepository selfIntroductionDetailRepository;
    private final UserRepository userRepository;

    public SelfIntroductionDto.SelfIntroductionListResponse findAllSelfIntroductions(Pageable pageable, Long userId) {
        int page = pageable.getPageNumber() - 1;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
        Page<SelfIntroduction> selfIntroductionPage = selfIntroductionRepository.findAllByUser(PageRequest.of(page, pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")), user);
        List<SelfIntroductionDto.Response> selfIntroductionList = selfIntroductionPage.stream().map(SelfIntroduction::toSelfIntroductionResponse).toList();
        return new SelfIntroductionDto.SelfIntroductionListResponse(selfIntroductionList, selfIntroductionPage.getTotalPages());
    }

    @Transactional
    public void saveSelfIntroduction(SelfIntroductionDto.CreateRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
        SelfIntroduction selfIntroduction = selfIntroductionRepository.save(
                SelfIntroduction.builder()
                        .user(user)
                        .title(request.getTitle())
                        .build());

        for (SelfIntroductionDto.SelfIntroductionDetailRequest selfIntroductionDetail : request.getDetailList()) {
            selfIntroductionDetailRepository.save(SelfIntroductionDetail
                    .builder()
                    .title(selfIntroductionDetail.getTitle())
                    .content(selfIntroductionDetail.getContent())
                    .type(selfIntroductionDetail.getType())
                    .selfIntroduction(selfIntroduction)
                    .build());
        }
    }

    @Transactional
    public Long updateSelfIntroduction(SelfIntroductionDto.UpdateRequest request, Long id) {
        SelfIntroduction selfIntroduction = selfIntroductionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 자소서가 존재하지 않습니다."));
        selfIntroduction.changeTitle(request.getTitle());
        selfIntroductionDetailRepository.deleteBySelfIntroduction(selfIntroduction);

        for (SelfIntroductionDto.SelfIntroductionDetailRequest selfIntroductionDetail : request.getDetailList()) {
            selfIntroductionDetailRepository.save(SelfIntroductionDetail
                    .builder()
                    .title(selfIntroductionDetail.getTitle())
                    .content(selfIntroductionDetail.getContent())
                    .type(selfIntroductionDetail.getType())
                    .selfIntroduction(selfIntroduction)
                    .build());
        }

        return selfIntroduction.getId();
    }

    @Transactional
    public void deleteSelfIntroduction(Long id) {
        SelfIntroduction selfIntroduction = selfIntroductionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 자소서가 존재하지 않습니다."));
        selfIntroductionRepository.delete(selfIntroduction);
    }
}
