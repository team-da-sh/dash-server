package be.dash.dashserver.database.core.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankJpaEntityRepository extends JpaRepository<BankJpaEntity, Long> {
}
