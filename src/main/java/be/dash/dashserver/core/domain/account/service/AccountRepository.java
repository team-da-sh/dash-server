package be.dash.dashserver.core.domain.account.service;

import be.dash.dashserver.core.domain.account.Account;

public interface AccountRepository {

    Account findByMemberIdAndIsTeacherAccount(long memberId);
}
