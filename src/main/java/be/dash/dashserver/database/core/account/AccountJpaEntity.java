package be.dash.dashserver.database.core.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import be.dash.dashserver.core.domain.account.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountJpaEntity {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String depositor;

    @Column(nullable = false)
    private String accountNumber;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "is_teacher_account")
    private Boolean isTeacherAccount = false;  // 기본값: 일반 회원

    // 수강생 계좌도 이걸로 관리하게 될 수 있으니 boolean으로 학생이 등록한것인지, 선생이 등록한것인지 구분.
    public Account toDomain(BankJpaEntity bankJpaEntity) {
        return new Account(
                true,
                depositor,
                accountNumber,
                memberId,
                bankId,
                isTeacherAccount,
                bankJpaEntity.getBankImageUrl(),
                bankJpaEntity.getBankName()
        );
    }

    public AccountJpaEntity(Account account) {
        this.depositor = account.depositor();
        this.accountNumber = account.accountNumber();
        this.memberId = account.memberId();
        this.bankId = account.bankId();
        this.isTeacherAccount = account.isTeacherAccount();
    }
}
