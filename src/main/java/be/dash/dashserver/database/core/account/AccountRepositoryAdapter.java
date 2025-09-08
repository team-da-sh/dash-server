package be.dash.dashserver.database.core.account;

import java.util.Objects;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.account.Account;
import be.dash.dashserver.core.domain.account.service.AccountRepository;
import be.dash.dashserver.core.exception.DashException;
import be.dash.dashserver.core.exception.NotFoundException;
import be.dash.dashserver.database.core.lesson.LessonJpaEntity;
import be.dash.dashserver.database.core.lesson.LessonJpaRepository;
import be.dash.dashserver.database.core.teacher.TeacherJpaEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;
    private final BankJpaRepository bankJpaRepository;
    private final LessonJpaRepository lessonJpaRepository;

    @Override
    public Account findByMemberIdAndIsTeacherAccount(long memberId) {
        AccountJpaEntity account = accountJpaRepository.findByMemberIdAndIsTeacherAccount(memberId, true);
        if (Objects.isNull(account)) {
            return Account.empty();
        }
        BankJpaEntity bankJpaEntity = bankJpaRepository.findById(account.getBankId())
                .orElseThrow(() -> new NotFoundException("계좌에 일치하는 은행 정보가 존재하지 않습니다."));
        return account.toDomain(bankJpaEntity);
    }

    @Override
    public void saveByMemberIdAndIsTeacherAccount(Account command) {
        AccountJpaEntity entity = new AccountJpaEntity(command);
        accountJpaRepository.save(entity);
    }

    @Override
    public void updateByMemberIdAndIsTeacherAccount(Account command) {
        AccountJpaEntity entity = new AccountJpaEntity(command);
        accountJpaRepository.updateAccount(entity);
    }

    @Override
    public boolean existsByMemberIdAndIsTeacherAccount(long memberId, boolean isTeacherAccount) {
        return accountJpaRepository.existsByMemberIdAndIsTeacherAccount(memberId, isTeacherAccount);
    }

    @Override
    public Account findByLessonId(long lessonId) {
        LessonJpaEntity lessonJpaEntity = lessonJpaRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("일치하는 수업이 존재하지 않습니다."));
        TeacherJpaEntity teacher = lessonJpaEntity.getTeacher();
        Long memberId = teacher.getMember().getId();
        AccountJpaEntity accountJpaEntity = accountJpaRepository.findByMemberIdAndIsTeacherAccount(memberId, true);
        if (Objects.isNull(accountJpaEntity)) {
            throw new DashException("해당 수업에 등록된 계좌가 없습니다.");
        }
        BankJpaEntity bankJpaEntity = bankJpaRepository.findById(accountJpaEntity.getBankId())
                .orElseThrow(() -> new NotFoundException("계좌에 일치하는 은행 정보가 존재하지 않습니다."));
        return accountJpaEntity.toDomain(bankJpaEntity);
    }
}
