package be.dash.dashserver.api.core.teacher.dto;

import be.dash.dashserver.core.domain.account.service.dto.AccountResult;

public record TeacherAccountResponse(
        boolean isRegistered,
        String depositor,
        Long bankId,
        String bankName,
        String accountNumber
) {
    public static TeacherAccountResponse from(AccountResult accountResult) {
        return new TeacherAccountResponse(accountResult.isRegistered(), accountResult.depositor(), accountResult.bankId(), accountResult.bankName(), accountResult.accountNumber());
    }
}
