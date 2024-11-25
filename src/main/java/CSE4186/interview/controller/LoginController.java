package CSE4186.interview.controller;


import CSE4186.interview.annotation.LoginUser;
import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.login.Oauth2UserService;
import CSE4186.interview.service.UserService;
import CSE4186.interview.utils.ApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final Oauth2UserService OAuth2UserService;

    @PostMapping("/join")
    @Operation(summary = "Join User", description = "회원가입")
    public ApiUtil.ApiSuccessResult<String> join(@Valid @RequestBody UserDTO.JoinRequest request){
        userService.join(request);
        return ApiUtil.success("회원가입이 완료되었습니다.");
    }

    @PostMapping("/join/check")
    @Operation(summary = "checkNameAndEmail", description = "네임, 이메일 중복 체크")
    public ApiUtil.ApiSuccessResult<Boolean> check(@Valid @RequestBody UserDTO.JoinRequest request){
        return ApiUtil.success(userService.isDuplicatedNameOrEmail(request.getName(), request.getEmail()));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "로그인")
    public ApiUtil.ApiSuccessResult<Map<String, String>> login(@LoginUser User loginUser) throws Exception {
        userService.checkAccountStatus(loginUser.getUsername());

        Map<String, String> userIdMap = new HashMap<>();
        userIdMap.put("userId", loginUser.getUsername());
        return ApiUtil.success(userIdMap);
    }

    //유효한 jwt 토큰인지 검사
    @GetMapping("/token/check")
    @Operation(summary="JWT validity check", description = "jwt 토큰 유효성 체크")
    public ApiUtil.ApiSuccessResult<String> tokenValidityCheck(HttpServletRequest request){
        return ApiUtil.success("");
    }

    @PostMapping("oauth2/google")
    @Operation(summary="google login", description = "구글에 로그인하여 받은 코드를 넘겨 로그인")
    public ApiUtil.ApiSuccessResult<Map<String,String>> getOauth2Token(@RequestBody UserDTO.Oauth2LoginRequest oauth2LoginRequest, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        //1. 코드를 받는다.
        String code= oauth2LoginRequest.getCode();
        //2. 코드를 사용해 토큰을 받아온다.
        String token= OAuth2UserService.requestGoogleToken(code);

        return ApiUtil.success(OAuth2UserService.requestGoogleAccountAndLogin(token, httpServletResponse));
    }
}