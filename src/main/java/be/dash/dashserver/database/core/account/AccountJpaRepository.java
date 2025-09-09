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
            SET a.depositor = :depositor,
                a.accountNumber = :accountNumber,
                a.bankId = :bankId
            WHERE a.memberId = :memberId
              AND a.isTeacherAccount = :isTeacherAccount
            """)
    void updateAccountByMemberIdAndType(@Param("depositor") String depositor,
                                        @Param("accountNumber") String accountNumber,
                                        @Param("bankId") Long bankId,
                                        @Param("memberId") Long memberId,
                                        @Param("isTeacherAccount") Boolean isTeacherAccount);
}
