package CSE4186.interview.login;

import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.jwt.TokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationManager customAuthenticationManager;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        logger.info("hi");
        try (InputStream is=request.getInputStream()){
            ObjectMapper objectMapper=new ObjectMapper();
            logger.info("login filter running...");
            JsonNode jsonNode=objectMapper.readTree(is);
            String username=jsonNode.get("email").asText();
            String password=jsonNode.get("password").asText();

            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            setDetails(request, authRequest);

            //authentication manager에게 토큰을 넘겨줌
            Authentication authentication=customAuthenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (IOException e) {
            logger.info("here");
            throw new AuthenticationServiceException("JSON 입력 형식 오류");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        logger.info("login success");

        //1. accessToken 생성
        String AccessJwt=tokenProvider.createAccessToken(authResult);

        //2. 헤더에 담는다.
        response.setHeader("Authorization","Bearer "+AccessJwt);

        //3. refreshToken 생성
        String refreshToken=tokenProvider.createRefreshToken(authResult,null);

        //4. 쿠키에 담기
        response.setHeader("Set-Cookie",
                "refreshToken="+refreshToken+"; "+
                        "Path=/;" +
                        "Domain=localhost; " +
                        "HttpOnly; " +
                        "Secure; "+
                        "Max-Age=604800; "
        );

        chain.doFilter(request,response);
    }

    //인증 실패 시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        // ResponseEntity를 사용하여 실패 메시지를 JSON 형태로 반환
        ResponseEntity<BaseResponseDto<String>> responseEntity = ResponseEntity.badRequest().body(
                new BaseResponseDto<>(
                        "fail",
                        failed.getMessage(),
                        ""
                ));

        String jsonResponse = objectMapper.writeValueAsString(responseEntity.getBody());
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }

}
