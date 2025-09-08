package be.dash.dashserver.database.core.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {

    AccountJpaEntity findByMemberIdAndIsTeacherAccount(long memberId, boolean isTeacherAccount);

    boolean existsByMemberIdAndIsTeacherAccount(long memberId, boolean isTeacherAccount);

    @Modifying
    @Query("""
                UPDATE AccountJpaEntity a
                SET a.depositor = :#{#account.depositor},
                    a.accountNumber = :#{#account.accountNumber},
                    a.bankId = :#{#account.bankId},
                    a.isTeacherAccount = :#{#account.isTeacherAccount}
                WHERE a.id = :#{#account.id}
            """)
    void updateAccount(@Param("account") AccountJpaEntity account);
}
