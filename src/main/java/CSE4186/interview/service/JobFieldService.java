package CSE4186.interview.service;

import CSE4186.interview.entity.JobField;
import CSE4186.interview.repository.JobFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobFieldService {
    private final JobFieldRepository jobFieldRepository;

    public List<JobField> getAllJobFieldList() {
        return jobFieldRepository.findAll();
    }
}
