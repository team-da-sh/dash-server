package be.dash.dashserver.api.core.member.dto;

import java.time.LocalDateTime;
import java.util.List;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.reservation.Reservations;

import static java.util.stream.Collectors.toList;

public record MyLessonDetailedResponse(long id,
                                       String name,
                                       String imageUrl,
                                       Genre genre,
                                       Level level,
                                       List<RoundResponse> rounds,
                                       String location,
                                       String detailedAddress,
                                       LocalDateTime startDateTime,
                                       LocalDateTime endDateTime,
                                       ApplyStatus applyStatus,
                                       List<MemberReservationResponse> students,
                                       int studentCount
) {
    public static MyLessonDetailedResponse from(Lesson lesson, List<Member> members, Reservations reservations) {
        List<MemberReservationResponse> memberReservationResponses = members.stream()
                .map(member -> MemberReservationResponse.from(member, reservations.findReservationByMemberId(member.getId())))
                .toList();
        return new MyLessonDetailedResponse(
                lesson.getId(),
                lesson.getName(),
                lesson.getRepresentativeImageUrl(),
                lesson.getGenre(),
                lesson.getLevel(),
                lesson.getRounds().getRounds().stream().map(RoundResponse::from).collect(toList()),
                lesson.getLocationName(),
                lesson.getDetailedAddress(),
                lesson.getRounds().getStartTime(),
                lesson.getRounds().getEndTime(),
                ApplyStatus.calculate(lesson.getStartTime(), lesson.getReservationCount(), lesson.getMaxReservationCount()),
                memberReservationResponses,
                members.size()
        );
    }
}
