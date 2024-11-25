package CSE4186.interview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class UserDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "userJoinRequest", description = "회원가입 Request DTO")
    public static class JoinRequest {
        private String name;
        private String email;
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "userLoginRequest", description = "로그인 Request DTO")
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "userOauth2LoginRequest", description = "구글 로그인 Request DTO")
    public static class Oauth2LoginRequest {
        private String code;
    }
}