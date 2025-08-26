package be.dash.dashserver.database.core.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {

    AccountJpaEntity findByMemberIdAndIsTeacherAccount(long memberId, boolean isTeacherAccount);

    boolean existsByMemberIdAndIsTeacherAccount(long memberId, boolean isTeacherAccount);
}
