package CSE4186.interview.service;

import CSE4186.interview.controller.dto.DislikeDto;
import CSE4186.interview.entity.Dislike;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.DislikeRepository;
import CSE4186.interview.repository.LikesRepository;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DislikeService {

    private final LikesRepository likesRepository;
    private final DislikeRepository dislikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public String addDislike(DislikeDto.CreteRequest request, Long postId) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다."));

        Optional<Dislike> findDislike = dislikeRepository.findAllByPostAndUser(post.getId(), findUser.getId());

        if (findDislike.isPresent()) {
            dislikeRepository.delete(findDislike.get());
            post.subDislikeCount();
            return "싫어요 취소";
        } else {
            likesRepository.findAllByPostAndUser(post.getId(), findUser.getId()).ifPresent(d -> {
                throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
            });
            dislikeRepository.save(Dislike.builder()
                    .user(findUser)
                    .post(post)
                    .build());
            post.addDislikeCount();
            return "싫어요 성공";
        }
    }
}
