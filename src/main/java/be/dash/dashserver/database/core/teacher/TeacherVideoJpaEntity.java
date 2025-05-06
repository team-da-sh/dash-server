package be.dash.dashserver.database.core.teacher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import be.dash.dashserver.database.core.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teacher_video")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class TeacherVideoJpaEntity extends BaseTimeEntity {

    @Id
    @Column(name = "teacher_video_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Getter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    public TeacherVideoJpaEntity(long teacherId, String videoUrl) {
        this.teacherId = teacherId;
        this.videoUrl = videoUrl;
    }
}
