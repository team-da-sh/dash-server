package be.dash.dashserver.api.core.member.dto;

import java.time.LocalDateTime;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;

public record MemberReservationResponse(long reservationId,
                                        String name,
                                        String phoneNumber,
                                        LocalDateTime reservationDateTime,
                                        ReservationStatus reservationStatus) {
    public static MemberReservationResponse fromApprove(Member member, Reservation reservation) {
        return new MemberReservationResponse(reservation.getId(), member.getName(), member.getPhoneNumber(), reservation.getCreatedAt(), reservation.getReservationStatus());
    }

    public static MemberReservationResponse fromCancel(Member member, Reservation reservation) {
        return new MemberReservationResponse(reservation.getId(), member.getName(), member.getPhoneNumber(), reservation.getUpdatedAt(), reservation.getReservationStatus());
    }
}
