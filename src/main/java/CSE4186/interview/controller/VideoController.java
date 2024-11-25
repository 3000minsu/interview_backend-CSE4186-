package CSE4186.interview.controller;

import CSE4186.interview.annotation.LoginUser;
import CSE4186.interview.controller.dto.VideoDto;
import CSE4186.interview.entity.Video;
import CSE4186.interview.service.VideoService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
@Tag(name = "Video", description = "Video API")
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    @Operation(summary = "Add Video", description = "비디오 생성")
    public ApiUtil.ApiSuccessResult<Long> createVideo(@Valid @RequestBody VideoDto.CreateRequest request) {
        Long videoId = videoService.addVideo(request);
        return ApiUtil.success(videoId);
    }

    @GetMapping("/list")
    @Operation(summary = "Get All Videos", description = "모든 비디오를 조회")
    public ApiUtil.ApiSuccessResult<VideoDto.VideoListResponse> getAllVideo(
            @LoginUser User loginUser,
            @PageableDefault(page = 1, size = 10) Pageable pageable
    ) {
        Long userId = Long.valueOf(loginUser.getUsername());
        return ApiUtil.success(videoService.findAllVideoByUser(pageable, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Video", description = "비디오 삭제")
    public ApiUtil.ApiSuccessResult<String> deleteVideo(@PathVariable(name = "id") Long id) {
        videoService.deleteVideo(id);
        return ApiUtil.success("비디오 삭제 성공");
    }
}
