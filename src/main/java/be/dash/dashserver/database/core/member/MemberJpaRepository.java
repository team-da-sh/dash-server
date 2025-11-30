package be.dash.dashserver.database.core.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.SocialProvider;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {
    Optional<MemberJpaEntity> findBySocialIdAndProvider(String socialId, SocialProvider provider);

    @Modifying
    @Query("update MemberJpaEntity m set m.role = :role where m.id = :id")
    void updateRole(Long id, Role role);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
            "FROM MemberJpaEntity m " +
            "WHERE m.phoneNumber = :phoneNumber " +
            "AND NOT ( m.isDeleted = TRUE AND m.id = :id)")
    boolean existsByPhoneNumber(Long id, String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long memberId);

    @Modifying
    @Query("update MemberJpaEntity m set m.isDeleted = true where m.id = :memberId")
    void withdraw(Long memberId);

    @Modifying
    @Query("update MemberJpaEntity m set m.isDeleted = false where m.id = :id")
    void rejoin(long id);
}
