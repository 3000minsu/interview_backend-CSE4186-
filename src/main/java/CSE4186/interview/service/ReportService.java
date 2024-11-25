package CSE4186.interview.service;

import CSE4186.interview.controller.dto.ReportDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.Report;
import CSE4186.interview.entity.User;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.CommentRepository;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.ReportRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public Long addReport(ReportDto.CreateRequest request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        Report.ReportBuilder reportBuilder = Report.builder()
                .user(findUser)
                .reportType(request.getReportType());

        if (request.getReportType().equals("post")) {
            Post post = postRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new NotFoundException("해당 게시물이 존재하지 않습니다."));
            reportBuilder.post(post);

        } else if (request.getReportType().equals("comment")) {
            Comment comment = commentRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new NotFoundException("해당 댓글이 존재하지 않습니다."));
            reportBuilder.comment(comment);
        }

        Report report = reportRepository.save(reportBuilder.build());

        checkReportCount(findUser);

        return report.getId();
    }

    private void checkReportCount(User user) {
        Long reportCount = reportRepository.findReportCountByUser(user.getId());
        if (reportCount >= 3) {
            user.accountSuspension();
        }
    }
}
