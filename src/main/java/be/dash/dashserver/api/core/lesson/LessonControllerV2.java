package be.dash.dashserver.api.core.lesson;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import be.dash.dashserver.api.core.lesson.dto.LessonAccount;
import be.dash.dashserver.api.core.lesson.dto.LessonAccountResponse;
import be.dash.dashserver.api.support.MemberId;
import be.dash.dashserver.core.domain.reservation.service.ReservationService;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/lessons")
@Validated
public class LessonControllerV2 {
    private final ReservationService reservationService;

    @PostMapping("/{lessonId}/reservations")
    public ResponseEntity<LessonAccountResponse> createReservation(
            @MemberId Long memberId,
            @PathVariable @Min(value = 1L, message = "수업의 식별자는 양수로 이루어져야 합니다.") long lessonId) {
        LessonAccount account = reservationService.reserve(memberId, lessonId);
        return ResponseEntity.ok(new LessonAccountResponse(account));
    }
}
