package CSE4186.interview.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private Key key;

    public TokenProvider(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] KeyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(KeyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        logger.info("TokenProvider-createAccessToken");
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)//GrantedAuthority 클래스 내의 getAuthority를 호출하여 이를 스트링 타입으로 변환
                .collect(Collectors.joining(",")); //얻은 스트링들을 ,로 연결한 후 반환
        logger.info(authorities);
        long now = (new Date()).getTime();
        Date validity = new Date(now + 60 * 60 * 1000); //60 min

        //jwt의 페이로드에 담기는 내용
        //claim: 사용자 권한 정보와 데이터를 일컫는 말
        return Jwts.builder()
                .setSubject(authentication.getName()) //토큰 제목
                .claim(AUTHORITIES_KEY, authorities) //토큰에 담길 내용
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        logger.info("TokenProvider-getAuthentication");
        Claims claims = Jwts
                .parserBuilder() //받은 token을 파싱할 수 있는 객체(JwtParserBuilder)를 리턴
                .setSigningKey(key) //ParserBuilder의 key 설정
                .build() //ParserBuilder을 통해 Parser 리턴.
                .parseClaimsJws(token) //토큰을 파싱하여
                .getBody(); //body를 리턴함

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateAccessToken(String token, HttpServletRequest httpServletRequest) throws IOException {
        logger.debug("TokenProvider-validateToken");
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
            httpServletRequest.setAttribute("exception", JwtExceptionCode.WRONG_TOKEN.getCode());
            System.out.println("1 JWT");
        } catch (ExpiredJwtException e) {
            logger.info("2 JWT");
            httpServletRequest.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
            System.out.println("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다");
            httpServletRequest.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
            System.out.println("3 JWT");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다");
            httpServletRequest.setAttribute("exception", JwtExceptionCode.ILLEGAL_TOKEN.getCode());
            System.out.println("4 JWT");
        }
        return false;
    }
}