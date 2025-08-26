package be.dash.dashserver.database.core.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankJpaRepository extends JpaRepository<BankJpaEntity, Long> {
}
