package be.dash.dashserver.api.core.member.dto;

import java.util.Comparator;
import java.util.List;
import be.dash.dashserver.core.domain.member.service.ReservationResult;

public record ReservationsResponse(List<ReservationResponse> reservations) {
    public static ReservationsResponse from(List<ReservationResult> reservations) {
        return new ReservationsResponse(
                reservations.stream()
                        .sorted(Comparator.comparing(ReservationResult::createdAt).reversed())
                        .map(ReservationResponse::from)
                        .toList()
        );
    }
}
