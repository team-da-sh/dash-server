package be.dash.dashserver.api.core.member.dto;

public record ReservationStatisticsResponse(int beforeLessonCount,
                                            int duringLessonCount,
                                            int afterLessonCount) {
}
