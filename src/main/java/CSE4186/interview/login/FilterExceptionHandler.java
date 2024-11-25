package CSE4186.interview.login;

import CSE4186.interview.jwt.JwtExceptionCode;
import CSE4186.interview.utils.ApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class FilterExceptionHandler extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        }
        catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            setErrorResponse(response, JwtExceptionCode.WRONG_TOKEN);
        }
        catch (ExpiredJwtException e) {
            setErrorResponse(response, JwtExceptionCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            setErrorResponse(response, JwtExceptionCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            setErrorResponse(response, JwtExceptionCode.ILLEGAL_TOKEN);
        }
    }

    private void setErrorResponse(HttpServletResponse response, JwtExceptionCode code){
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiUtil.ApiErrorResult<String> error= ApiUtil.error(code.getCode(),code.getMessage());

        try{
            response.getWriter().write(objectMapper.writeValueAsString(error));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
