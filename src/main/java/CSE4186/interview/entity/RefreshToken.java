package CSE4186.interview.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value="refreshToken",timeToLive = 5*60*60)
public class RefreshToken {
    @Id
    public String refreshToken;
    private String email;

    public RefreshToken(final String refreshToken, final String email){
        this.refreshToken=refreshToken;
        this.email=email;
    }
}
