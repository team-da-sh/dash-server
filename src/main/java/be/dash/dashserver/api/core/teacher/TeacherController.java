package be.dash.dashserver.api.core.teacher;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import be.dash.dashserver.api.core.member.MyLessonDetailedResponse;
import be.dash.dashserver.api.core.member.dto.MyLessonsResponse;
import be.dash.dashserver.api.core.member.dto.MyLessonsThumbnailResponse;
import be.dash.dashserver.api.core.account.dto.AccountRequest;
import be.dash.dashserver.api.core.teacher.dto.CreateTeacherRequest;
import be.dash.dashserver.api.core.teacher.dto.CreateTeacherResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherAccountResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherDetailResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherProfileDetailResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherProfileResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherResponses;
import be.dash.dashserver.api.core.teacher.dto.TeacherUpdateRequest;
import be.dash.dashserver.api.support.MemberId;
import be.dash.dashserver.api.support.Permission;
import be.dash.dashserver.core.domain.account.service.AccountService;
import be.dash.dashserver.core.domain.common.Keyword;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.teacher.TeacherLessonGenres;
import be.dash.dashserver.core.domain.teacher.service.TeacherService;
import be.dash.dashserver.core.domain.teacher.service.dto.TeacherDetailResult;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teachers")
@Validated
public class TeacherController {
    private final TeacherService teacherService;
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<TeacherResponses> search(@RequestParam(required = false, defaultValue = Keyword.ANY, name = "keyword") Keyword keyword) {
        List<TeacherLessonGenres> searched = teacherService.search(keyword);
        return ResponseEntity.ok(TeacherResponses.from(searched));
    }

    @Permission(role = Role.MEMBER)
    @PostMapping
    public ResponseEntity<CreateTeacherResponse> create(@MemberId Long memberId,
                                                        @Valid @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.ok(CreateTeacherResponse.from(teacherService.create(request.toCommand(memberId))));
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherDetailResponse> find(@PathVariable @Min(value = 1L, message = "댄서의 식별자는 양수로 이루어져야 합니다.") long teacherId) {
        TeacherDetailResult teacherDetailResult = teacherService.find(teacherId);
        return ResponseEntity.ok(new TeacherDetailResponse(teacherDetailResult));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me")
    public ResponseEntity<TeacherProfileResponse> findMyTeacherProfile(@MemberId Long memberId) {
        return ResponseEntity.ok(TeacherProfileResponse.from(teacherService.findMyTeacherProfile(memberId)));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/detail")
    public ResponseEntity<TeacherProfileDetailResponse> findMyTeacherDetail(@MemberId Long memberId) {
        return ResponseEntity.ok(TeacherProfileDetailResponse.from(teacherService.findMyTeacherProfileDetail(memberId)));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/account")
    public ResponseEntity<TeacherAccountResponse> findMyTeacherAccount(@MemberId Long memberId) {
        return ResponseEntity.ok(TeacherAccountResponse.from(accountService.findMyTeacherAccount(memberId)));
    }

    @Permission(role = Role.TEACHER)
    @PostMapping("/me/account")
    public ResponseEntity<Void> registerMyTeacherAccount(@MemberId Long memberId, @RequestBody @Valid AccountRequest teacherAccountRequest) {
        accountService.registerMyTeacherAccount(memberId, teacherAccountRequest);
        return ResponseEntity.ok().build();
    }

    @Permission(role = Role.TEACHER)
    @PatchMapping("/me")
    public ResponseEntity<Void> updateTeacherProfile(@MemberId Long memberId,
                                                     @Valid @RequestBody TeacherUpdateRequest request) {
        teacherService.updateTeacherProfile(request.toCommand(memberId));
        return ResponseEntity.noContent().build();
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/lessons/{lessonId}")
    public ResponseEntity<MyLessonDetailedResponse> getMyLesson(@MemberId Long memberId, @PathVariable Long lessonId) {
        return ResponseEntity.ok(teacherService.getMyLesson(memberId, lessonId));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/lessons")
    public ResponseEntity<MyLessonsResponse> getMyLessons(@MemberId Long memberId) {
        return ResponseEntity.ok(teacherService.getMyLessons(memberId));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/lessons/thumbnails")
    public ResponseEntity<MyLessonsThumbnailResponse> getMyLessonsThumbnail(@MemberId Long memberId) {
        return ResponseEntity.ok(teacherService.getMyLessonsThumbnail(memberId));
    }
}
