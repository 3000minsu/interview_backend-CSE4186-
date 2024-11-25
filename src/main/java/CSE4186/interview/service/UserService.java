package CSE4186.interview.service;

import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.entity.User;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.login.Role;
import CSE4186.interview.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
    }

    @Transactional
    public void join(UserDTO.JoinRequest request) {
        String encodedPassword=bCryptPasswordEncoder.encode(request.getPassword());
        User newUser=new User(request.getName(), request.getEmail(), encodedPassword, Role.USER);
        userRepository.save(newUser);
    }

    public String getUserId(String email) throws JsonProcessingException {
        Map<String, Long> userIdMap=new HashMap<>();
        Optional<User> user=userRepository.findByEmail(email);

        if(user.isPresent()){
            userIdMap.put("userId",user.get().getId());
        }

        return objectMapper.writeValueAsString(userIdMap);
    }

    public Boolean isDuplicatedNameOrEmail(String name, String email) {
        return userRepository.findByName(name).isPresent() || userRepository.findByEmail(email).isPresent();
    }

    public void checkAccountStatus(String userId) {
        Long id = Long.parseLong(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        if (user.isSuspended()) {
            throw new IllegalStateException("해당 계정은 정지된 계정입니다.");
        }
    }

    public String checkLikeOrDislike(Long postId, Long id) {
        return userRepository.findUserByLikesAndDislike(postId, id);
    }
}