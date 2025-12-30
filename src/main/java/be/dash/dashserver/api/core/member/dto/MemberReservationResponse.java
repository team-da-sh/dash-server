package be.dash.dashserver.api.core.member.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;

public record MemberReservationResponse(long reservationId,
                                        String name,
                                        String phoneNumber,
                                        LocalDateTime reservationDateTime,
                                        ReservationStatus reservationStatus) {
    
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    
    public static MemberReservationResponse fromApprove(Member member, Reservation reservation) {
        return new MemberReservationResponse(
                reservation.getId(), 
                member.getName(), 
                member.getPhoneNumber(), 
                toKst(reservation.getCreatedAt()), 
                reservation.getReservationStatus());
    }

    public static MemberReservationResponse fromCancel(Member member, Reservation reservation) {
        return new MemberReservationResponse(
                reservation.getId(), 
                member.getName(), 
                member.getPhoneNumber(), 
                toKst(reservation.getUpdatedAt()), 
                reservation.getReservationStatus());
    }
    
    private static LocalDateTime toKst(LocalDateTime utcTime) {
        if (utcTime == null) return null;
        ZonedDateTime utcZoned = utcTime.atZone(UTC);
        return utcZoned.withZoneSameInstant(KST).toLocalDateTime();
    }
}
