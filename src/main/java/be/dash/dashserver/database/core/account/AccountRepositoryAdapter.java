package be.dash.dashserver.database.core.account;

import java.util.Objects;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.account.Account;
import be.dash.dashserver.core.domain.account.service.AccountRepository;
import be.dash.dashserver.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private AccountJpaEntityRepository accountJpaEntityRepository;
    private BankJpaEntityRepository bankJpaEntityRepository;

    @Override
    public Account findByMemberIdAndIsTeacherAccount(long memberId) {
        AccountJpaEntity account = accountJpaEntityRepository.findByMemberIdAndIsTeacherAccount(memberId, true);
        if (Objects.isNull(account)) {
            return Account.empty();
        }
        BankJpaEntity bankJpaEntity = bankJpaEntityRepository.findById(account.getBankId())
                .orElseThrow(() -> new NotFoundException("계좌에 일치하는 은행 정보가 존재하지 않습니다."));
        return account.toDomain(bankJpaEntity);
    }

    @Override
    public void saveByMemberIdAndIsTeacherAccount(Account command) {
        AccountJpaEntity entity = new AccountJpaEntity(command);
        accountJpaEntityRepository.save(entity);
    }

    @Override
    public boolean existsByMemberIdAndIsTeacherAccount(long memberId, boolean isTeacherAccount) {
        return accountJpaEntityRepository.existsByMemberIdAndIsTeacherAccount(memberId, isTeacherAccount);
    }
}
