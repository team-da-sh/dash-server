package be.dash.dashserver.core.domain.reservation.command;

import be.dash.dashserver.core.domain.reservation.ReservationStatus;

public record CancelReservationCommand(ReservationStatus reservationStatus,
                                       long bankId,
                                       String bankName,
                                       String accountNumber) {
}
