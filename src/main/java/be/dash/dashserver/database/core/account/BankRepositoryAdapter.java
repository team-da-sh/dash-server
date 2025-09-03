package be.dash.dashserver.database.core.account;

import java.util.List;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.account.Bank;
import be.dash.dashserver.core.domain.account.service.BankRepository;
import be.dash.dashserver.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BankRepositoryAdapter implements BankRepository {
    private final BankJpaRepository bankJpaRepository;

    @Override
    public Bank findById(long bankId) {
        BankJpaEntity bank = bankJpaRepository.findById(bankId)
                .orElseThrow(() -> new NotFoundException("해당하는 은행을 찾을 수 없습니다."));
        return bank.toDomain();
    }

    @Override
    public List<Bank> findAll() {
        return bankJpaRepository.findAll()
                .stream().map(BankJpaEntity::toDomain).toList();
    }
}
