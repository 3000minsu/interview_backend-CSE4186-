package CSE4186.interview.login;

import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.entity.User;
import CSE4186.interview.jwt.JwtFilter;
import CSE4186.interview.jwt.TokenProvider;
import CSE4186.interview.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class Oauth2UserService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final String client_id;
    private final String client_secret;
    private final String redirect_uri;
    public static final String AUTHORIZATION_HEADER="Authorization";
    private static final Logger logger= LoggerFactory.getLogger(JwtFilter.class);

    public Oauth2UserService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
                             @Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret,
                             @Value("${spring.security.oauth2.client.registration.google.redirect-uri}") String redirectUri,
                             ObjectMapper objectMapper,
                             UserRepository userRepository, TokenProvider tokenProvider){
        client_id=clientId;
        client_secret=clientSecret;
        redirect_uri=redirectUri;
        this.objectMapper=objectMapper;
        this.userRepository=userRepository;
        this.tokenProvider = tokenProvider;
    }

    private org.springframework.security.core.userdetails.User createUser(String email, User user) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(Collections.singleton(new SimpleGrantedAuthority(Role.getIncludingRoles("USER"))));

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getPassword(),
                grantedAuthorities
        );
    }

    private Authentication authenticate(UserDetails user) {
        return new UsernamePasswordAuthenticationToken(user,user.getPassword(),user.getAuthorities());
    }

    public String requestGoogleToken(String code) throws JsonProcessingException {
        //코드 인코딩
        code = URLDecoder.decode(code, StandardCharsets.UTF_8);

        //token을 받아올 url
        String url="https://oauth2.googleapis.com/token";

        //통신 수단
        RestTemplate template=new RestTemplate();

        //헤더 설정
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //body설정
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("code", code);
        params.add("redirect_uri", redirect_uri);

        HttpEntity<MultiValueMap<String,String>>requestEntity=new HttpEntity<>(params,headers);

        //토큰 받아오기
        String response=template.postForObject(url,requestEntity,String.class);
        Map<String,String> responseBody=objectMapper.readValue(response,new TypeReference<Map<String,String>>(){});
        logger.info("responseBody : "+responseBody);
        String token=responseBody.get("access_token");
        logger.info("token : "+token);
        return token;
    }
    public Map<String,String> requestGoogleAccountAndLogin(String token, HttpServletResponse httpServletResponse) {

        //1. 구글에 email 요청
        String url = "https://www.googleapis.com/userinfo/v2/me";

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        //새 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        //토큰은 헤더에 포함
        headers.add("Authorization","Bearer "+token);
        //request entity 생성
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        //get 요청 보내기
        ResponseEntity<String> responseEntity = template.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class);
        String result = responseEntity.getBody();

        //2. db에 존재하는 email인지 체크
        try{
            //응답에서 email 가져오기
            Map<String, String> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, String>>() {});
            String email=jsonMap.get("email");
            String name=jsonMap.get("name");

            //db에 email이 존재하는지 체크
            User user=userRepository.findByEmail(email)
                    .orElseGet(()-> {
                        User newUser = new User(name, email, Role.USER);
                        userRepository.save(newUser);
                        return newUser;
                    });

            //user 객체 만들기
            org.springframework.security.core.userdetails.User User=createUser(email,user);
            //인증하기
            Authentication authentication=authenticate(User);
            //SecurityContextHolder에 저장하기
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //accessToken 발급받아 헤더에 세팅하기
            String accessToken=tokenProvider.createAccessToken(authentication);
            httpServletResponse.setHeader(AUTHORIZATION_HEADER,"Bearer "+accessToken);

            //refreshToken 발급받아 쿠키에 세팅하기
            String refreshToken=tokenProvider.createRefreshToken(authentication,null);
            httpServletResponse.setHeader("Set-Cookie",
                    "refreshToken="+refreshToken+"; "+
                            "Path=/;" +
                            "Domain=localhost; " +
                            "HttpOnly; " +
                            "Max-Age=604800; "
            );

            //유저 아이디
            Map<String,String> userIdMap=new HashMap<>();
            userIdMap.put("userId", String.valueOf(user.getId()));
            //리턴
            return userIdMap;

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
