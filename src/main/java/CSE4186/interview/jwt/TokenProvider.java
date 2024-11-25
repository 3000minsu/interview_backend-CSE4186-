package CSE4186.interview.jwt;

import CSE4186.interview.repository.RefreshTokenRepository;
import CSE4186.interview.service.TokenRefreshService;
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
    private final TokenRefreshService tokenRefreshService;
    private Key key;

    public TokenProvider(@Value("${jwt.secret}") String secret, TokenRefreshService tokenRefreshService) {
        this.secret = secret;
        this.tokenRefreshService = tokenRefreshService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] KeyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(KeyBytes);
    }

    private String createToken(Authentication authentication, Integer tokenValidityInMilliseconds){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)//GrantedAuthority 클래스 내의 getAuthority를 호출하여 이를 스트링 타입으로 변환
                .collect(Collectors.joining(",")); //얻은 스트링들을 ,로 연결한 후 반환
        logger.info(authorities);
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds); //60 min

        //jwt의 페이로드에 담기는 내용
        //claim: 사용자 권한 정보와 데이터를 일컫는 말
        return Jwts.builder()
                .setSubject(authentication.getName()) //토큰 제목
                .claim(AUTHORITIES_KEY, authorities) //토큰에 담길 내용
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication,60*60*1000); //60 min
    }

    public String createRefreshToken(Authentication authentication, String prevRefreshToken) {
        //1. refreshToken을 만든다
        String refreshToken=createToken(authentication,5*60*60*1000); //5 hour
        //String refreshToken=createToken(authentication,2*60*1000); //5 hour

        //2. redisDB에 저장한다.
        tokenRefreshService.updateRefreshToken(prevRefreshToken,refreshToken,authentication.getName());

        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        logger.info("TokenProvider-getAuthentication");
        Claims claims = Jwts
                .parserBuilder() //받은 token을 파싱할 수 있는 객체(JwtParserBuilder)를 리턴
                .setSigningKey(key) //ParserBuilder의 key 설정
                .build() //ParserBuilder을 통해 Parser 리턴.
                .parseClaimsJws(token) //토큰을 파싱하여
                .getBody(); //body를 리턴함

        logger.info(claims.get(AUTHORITIES_KEY).toString());

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token, HttpServletRequest httpServletRequest) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    }

    public boolean checkRefreshToken(String refreshToken) {
        //1. refreshToken에서 username을 가져온다.
        //1.1 토큰에서 claims 가져오기
        Claims claims=Jwts
                .parserBuilder() //받은 token을 파싱할 수 있는 객체(JwtParserBuilder)를 리턴
                .setSigningKey(key) //ParserBuilder의 key 설정
                .build() //ParserBuilder을 통해 Parser 리턴.
                .parseClaimsJws(refreshToken) //토큰을 파싱하여
                .getBody(); //body를 리턴함

        //1.2 email 가져오기
        String email=claims.getSubject();

        //2. redisDB에 저장된 refreshToken과 현 refreshToken을 비교한다.
        return tokenRefreshService.match(refreshToken,email);
    }
}