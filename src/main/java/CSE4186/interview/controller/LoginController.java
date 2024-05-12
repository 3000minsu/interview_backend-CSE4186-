package CSE4186.interview.controller;


import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.service.UserService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "Login API")
public class LoginController {

    private final UserService userService;

    @PostMapping("/join")
    @Operation(summary = "Join User", description = "회원가입")
    public ApiUtil.ApiSuccessResult<String> join(@Valid @RequestBody UserDTO.joinRequest request){
        userService.join(request);
        return ApiUtil.success("회원가입이 완료되었습니다.");
    }

    @PostMapping("/join/check")
    @Operation(summary = "checkNameAndEmail", description = "네임, 이메일 중복 체크")
    public ApiUtil.ApiSuccessResult<Boolean> check(@Valid @RequestBody UserDTO.joinRequest request){
        Boolean result = userService.isDuplicatedNameOrEmail(request.getName(), request.getEmail());
        return ApiUtil.success(result);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "로그인")
    public ApiUtil.ApiSuccessResult<Map<String, String>> login(@AuthenticationPrincipal User loginUser) throws Exception {
        userService.checkAccountStatus(loginUser.getUsername());

        Map<String, String> userIdMap = new HashMap<>();
        userIdMap.put("userId", loginUser.getUsername());
        return ApiUtil.success(userIdMap);
    }

    //유효한 jwt 토큰인지 검사
    @GetMapping("/token/check")
    @Operation(summary="JWT validity check", description = "jwt 토큰 유효성 체크")
    public ResponseEntity<BaseResponseDto<String>> tokenValidityCheck(HttpServletRequest request){
        String exceptionCode = (String) request.getAttribute("exception");
        return ResponseEntity.ok(
                new BaseResponseDto<>(
                        "success",
                        exceptionCode.equals(null) ? "" : exceptionCode,
                        ""
                ));
    }
}