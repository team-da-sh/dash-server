package be.dash.dashserver.core.domain.account.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import be.dash.dashserver.core.domain.account.Account;
import be.dash.dashserver.core.domain.account.service.dto.AccountResult;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountResult findMyTeacherAccount(long memberId) {
        Account account = accountRepository.findByMemberIdAndIsTeacherAccount(memberId);
        return new AccountResult(account);
    }
}
