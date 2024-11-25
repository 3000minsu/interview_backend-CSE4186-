package CSE4186.interview.service;

import CSE4186.interview.controller.dto.VideoDto;
import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.PostVideoRepository;
import CSE4186.interview.repository.UserRepository;
import CSE4186.interview.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final UserRepository userRepository;
    private final PostVideoRepository postVideoRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public Long addVideo(VideoDto.CreateRequest request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        Video video = videoRepository.save(request.toEntity(findUser));
        return video.getId();
    }

    public VideoDto.VideoListResponse findAllVideoByUser(Pageable pageable, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
        int page = pageable.getPageNumber() - 1;
        Page<Video> videoPage = videoRepository.findAllByUser(PageRequest.of(page, pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")), findUser);
        return new VideoDto.VideoListResponse(videoPage.stream().map(Video::toVideoResponse).toList(), videoPage.getTotalPages());
    }

    @Transactional
    public void deleteVideo(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 비디오가 존재하지 않습니다."));

        postVideoRepository.deleteByVideo(video);
        videoRepository.deleteById(id);
    }
}
