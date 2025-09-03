package be.dash.dashserver.database.core.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import be.dash.dashserver.core.domain.account.Bank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bank")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankJpaEntity {
    @Id
    @Column(name = "bank_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bankImageUrl;

    @Column(nullable = false)
    private String bankName;

    public BankJpaEntity(Long id, String bankImageUrl, String bankName) {
        this.id = id;
        this.bankImageUrl = bankImageUrl;
        this.bankName = bankName;
    }

    public Bank toDomain() {
        return new Bank(id, bankImageUrl, bankName);
    }
}
