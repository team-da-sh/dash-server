package be.dash.dashserver.api.core.member.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.command.CancelReservationCommand;

public record ReservationCancelRequest(
        @NotNull(message = "입금 여부는 필수 항목입니다.")
        Boolean deposited,
        @Nullable
        Long bankId,
        @Nullable
        String bankName,
        @Pattern(regexp = "\\d{1,20}", message = "계좌번호는 숫자 1~20자리여야 합니다")
        String accountNumber
) {
    public CancelReservationCommand toCancelReservationCommand() {
        return new CancelReservationCommand(ReservationStatus.match(deposited), bankId, bankName, accountNumber);
    }
}
