package be.dash.dashserver.core.domain.account.service;

import be.dash.dashserver.core.domain.account.Account;

public interface AccountRepository {

    Account findByMemberIdAndIsTeacherAccount(long memberId);

    void saveByMemberIdAndIsTeacherAccount(Account account);

    void updateByMemberIdAndIsTeacherAccount(Account account);

    boolean existsByMemberIdAndIsTeacherAccount(long memberId, boolean isTeacherAccount);

    Account findByLessonId(long lessonID);
}
