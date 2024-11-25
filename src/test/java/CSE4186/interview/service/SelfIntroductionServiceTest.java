package CSE4186.interview.service;

import CSE4186.interview.repository.SelfIntroductionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SelfIntroductionServiceTest {

    @InjectMocks
    private SelfIntroductionService selfIntroductionService;

    @Mock
    private SelfIntroductionRepository selfIntroductionRepository;

    @Test
    void findAllSelfIntroductions() {
    }

    @Test
    void save() {
    }

    @Test
    void updateSelfIntroduction() {
    }

    @Test
    void deleteSelfIntroduction() {
    }
}