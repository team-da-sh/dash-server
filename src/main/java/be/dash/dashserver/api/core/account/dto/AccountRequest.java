package be.dash.dashserver.api.core.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import be.dash.dashserver.core.domain.account.Account;

public record AccountRequest(
        @NotBlank
        String depositor,
        @NotNull
        Long bankId,
        @NotBlank
        String bankName,
        @Pattern(regexp = "\\d{1,20}", message = "계좌번호는 숫자 1~20자리여야 합니다")
        String accountNumber
) {
    public Account toCommand(long memberId, boolean isTeacherAccount) {
        return new Account(false, depositor, accountNumber, memberId, bankId, isTeacherAccount, null, null);
    }
}
