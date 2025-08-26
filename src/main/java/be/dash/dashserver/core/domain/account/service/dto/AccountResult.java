package be.dash.dashserver.core.domain.account.service.dto;

import be.dash.dashserver.core.domain.account.Account;

public record AccountResult(
        boolean isRegistered,
        String depositor,
        Long bankId,
        String accountNumber,
        String bankName,
        String bankImageUrl
) {
    public AccountResult(Account account) {
        this(account.isRegistered(), account.depositor(), account.bankId(), account.accountNumber(), account.bankName(), account.bankImageUrl());
    }
}
