package be.dash.dashserver.database.core.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.database.core.common.BaseCreatedAtEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lessonId", "memberId"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationJpaEntity extends BaseCreatedAtEntity {

    @Id
    @Column(name = "reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "member_id")
    private Long memberId;

    public ReservationJpaEntity(Long lessonId, Long memberId) {
        this.lessonId = lessonId;
        this.memberId = memberId;
    }

    public Reservation toDomain() {
        return new Reservation(id, lessonId, memberId, getCreatedAt());
    }
}
