package CSE4186.interview.controller;


import CSE4186.interview.entity.JobField;
import CSE4186.interview.service.JobFieldService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/field")
@Tag(name = "JobField", description = "JobField API")
public class JobFieldController {

    private final JobFieldService jobFieldService;


    @GetMapping("/list")
    @Operation(summary = "Get All JobField", description = "모든 직무를 조회")
    public ApiUtil.ApiSuccessResult<List<JobField>> getAllList() {
        return ApiUtil.success(jobFieldService.getAllJobFieldList());
    }
}
