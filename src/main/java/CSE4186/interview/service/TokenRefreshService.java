package CSE4186.interview.service;

import CSE4186.interview.entity.RefreshToken;
import CSE4186.interview.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {
    private final RefreshTokenRepository refreshTokenRepository;

    public boolean match(String token, String subject) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(token);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            return refreshToken.getEmail().equals(subject);
        } else {
            return false;
        }
    }

    public void updateRefreshToken(String prevRefreshToken, String newRefreshToken, String nickname){
        if(prevRefreshToken!=null) refreshTokenRepository.deleteById(prevRefreshToken);
        RefreshToken refreshToken=new RefreshToken(newRefreshToken,nickname);
        refreshTokenRepository.save(refreshToken);
    }

}
