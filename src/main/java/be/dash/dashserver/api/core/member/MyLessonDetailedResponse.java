package be.dash.dashserver.api.core.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import be.dash.dashserver.api.core.member.dto.ApplyStatus;
import be.dash.dashserver.api.core.member.dto.MemberReservationResponse;
import be.dash.dashserver.api.core.member.dto.RoundResponse;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.reservation.Reservation;
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
                IntStream.range(0, members.size())
                        .mapToObj(i -> MemberReservationResponse.from(members.get(i), reservations.getReservations().get(i))).toList(),
                members.size()
        );
    }
}
