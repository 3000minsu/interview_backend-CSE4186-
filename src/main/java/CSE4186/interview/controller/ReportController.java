package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.ReportDto;
import CSE4186.interview.service.ReportService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "Report", description = "Report API")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "Create Report", description = "신고하기")
    public ApiUtil.ApiSuccessResult<Long> createReport(@Valid @RequestBody ReportDto.CreateRequest request) {
        Long reportId = reportService.addReport(request);
        return ApiUtil.success(reportId);
    }
}
