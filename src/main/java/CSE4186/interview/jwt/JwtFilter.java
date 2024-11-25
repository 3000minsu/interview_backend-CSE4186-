package CSE4186.interview.jwt;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger= LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER="Authorization";
    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider=tokenProvider;
    }

    private String resolveAccessToken(HttpServletRequest httpServletRequest) {
        logger.info("resolve AccessToken");
        String bearerToken= httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        //jwt 토큰은 bearer로 시작함
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            logger.info("JwtFilter-ResolveToken:succeed");
            //bearer 뒤부터 유의미한 정보이기 때문에
            return bearerToken.substring(7);
        }
        logger.info("failed..");
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest httpServletRequest){
        logger.info("resolve RefreshToken");
        Cookie[] cookies=httpServletRequest.getCookies();
        String resolveToken=null;
        if(cookies!=null){
            for(Cookie cookie:cookies) if(cookie.getName().equals("refreshToken")) resolveToken=cookie.getValue();
        }
        return resolveToken;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken=resolveAccessToken(request);
        String requestURI=request.getRequestURI();
        logger.info("jwt filter running...");

        //1. accessToken을 가지는지
        if(StringUtils.hasText(accessToken)){
            //2. accessToken이 유효하다면 pass
                if(tokenProvider.validateToken(accessToken,request)){
                    //2.1 securityContext에 저장한다.
                    SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(accessToken));
                }

                //2. accessToken이 유효하지 않다면 refreshToken의 유효성 검사
                else{
                    //2.1 accessToken의 유효기간이 지난 경우
                    if(String.valueOf(request.getAttribute("exception")).equals("expired token.")){
                        //2.2 refreshToken을 가진다면
                        String refreshToken=resolveRefreshToken(request);
                        if(refreshToken!=null){
                            //2.3 refreshToken이 유효하다면 & 서버에서 발급한 토큰이 맞다면
                            if(tokenProvider.validateToken(refreshToken,request) && tokenProvider.checkRefreshToken(refreshToken)){
                                //2.4 새로운 accessToken을 발급해준다
                                accessToken= tokenProvider.createAccessToken(tokenProvider.getAuthentication(refreshToken));

                                //2.5 새로운 refreshToken을 발급해준다
                                refreshToken= tokenProvider.createRefreshToken(tokenProvider.getAuthentication(refreshToken),refreshToken);

                                //2.6 accessToken을 헤더에 담는다.
                                response.setHeader(AUTHORIZATION_HEADER,"Bearer "+accessToken);

                                //2.7 refreshToken을 쿠키에 담는다.
                                response.setHeader("Set-Cookie",
                                        "refreshToken="+refreshToken+"; "+
                                                "Path=/;" +
                                                "Domain=localhost; " +
                                                "HttpOnly; " +
                                                "Max-Age=604800; "
                                );

                                //2.8 securityContext에 저장한다
                                SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(refreshToken));
                            }
                        }
                    }
                }
            }
        filterChain.doFilter(request,response);
    }

}