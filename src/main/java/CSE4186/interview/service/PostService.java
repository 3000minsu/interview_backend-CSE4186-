package CSE4186.interview.service;

import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.*;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final ReportRepository reportRepository;
    private final PostVideoRepository postVideoRepository;
    private final JobFieldRepository jobFieldRepository;

    @Transactional(readOnly = true)
    public PostDto.PostListResponse findPostsByCondition(Pageable pageable, String q, String condition) {
        int page = pageable.getPageNumber() - 1;
        Page<Post> posts = postRepository.findPostsBySearchCondition(q, condition, PageRequest.of(page, pageable.getPageSize()));
        List<PostDto.Response> postList = posts.stream()
                .map(Post::toPostResponse)
                .toList();

        return new PostDto.PostListResponse(postList, posts.getTotalPages());
    }

    public void addPost(PostDto.CreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        JobField jobField = jobFieldRepository.findById(request.getJobFieldId()).get();

        Post post = postRepository.save(request.toEntity(user, jobField));

        createPostVideo(request.getVideoIdList(), post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + id));

        List<Report> reports = reportRepository.findReportByPost(post);
        reports.forEach(Report::removeParentRelation);

        postRepository.delete(post);
    }

    public void updatePost(Long id, PostDto.UpdateRequest request) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + id));
        post.updatePost(request.getTitle(), request.getContent(), jobFieldRepository.findById(request.getJobFieldId()).get());
        postVideoRepository.deleteByPost(post);

        createPostVideo(request.getVideoIdList(), post);
    }

    public PostDto.Response findPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + id));
        post.addViewCount();

        return post.toPostResponse();
    }

    private void createPostVideo(List<Long> request, Post post) {
        for (Long videoId : request) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new NotFoundException("해당 비디오가 존재하지 않습니다."));

            postVideoRepository.save(PostVideo.builder()
                    .video(video)
                    .post(post)
                    .build());
        }
    }
}
