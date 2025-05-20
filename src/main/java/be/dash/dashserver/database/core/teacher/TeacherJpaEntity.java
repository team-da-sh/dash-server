package be.dash.dashserver.database.core.teacher;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.database.core.common.BaseTimeEntity;
import be.dash.dashserver.database.core.member.MemberJpaEntity;
import be.dash.dashserver.database.core.teacher.support.StringListConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "teacher")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeacherJpaEntity extends BaseTimeEntity {

    @Id
    @Column(name = "teacher_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberJpaEntity member;

    @Column(nullable = false, length = 500)
    private String detail;

    @Convert(converter = StringListConverter.class)
    @Column(nullable = true)
    private List<String> education;

    @Convert(converter = StringListConverter.class)
    @Column(nullable = true)
    private List<String> experience;

    @Convert(converter = StringListConverter.class)
    @Column(nullable = true)
    private List<String> prize;

    @Column(nullable = true, unique = true, columnDefinition = "TEXT")
    private String instagram;

    @Column(nullable = true, unique = true, columnDefinition = "TEXT")
    private String youtube;

    @Builder
    public TeacherJpaEntity(Long id, MemberJpaEntity member, String detail,
                            List<String> education, List<String> experience,
                            String instagram, String youtube, List<String> prize) {
        this.member = member;
        this.detail = detail;
        this.education = education;
        this.experience = experience;
        this.instagram = instagram;
        this.youtube = youtube;
        this.prize = prize;
    }

    public TeacherJpaEntity(Teacher teacher) {
        this.id = teacher.getId();
        this.member = new MemberJpaEntity(teacher.getMember());
        this.detail = teacher.getDetail();
        this.education = teacher.getEducations();
        this.experience = teacher.getExperiences();
        this.prize = teacher.getPrizes();
        this.instagram = teacher.getInstagram();
        this.youtube = teacher.getYoutube();
    }

    public static TeacherJpaEntity fromDomain(Teacher teacher) {
        return TeacherJpaEntity.builder()
                .member(new MemberJpaEntity(teacher.getMember()))
                .detail(teacher.getDetail())
                .education(teacher.getEducations())
                .experience(teacher.getExperiences())
                .prize(teacher.getPrizes())
                .instagram(teacher.getInstagram())
                .youtube(teacher.getYoutube())
                .build();
    }

    public Teacher toDomain() {
        return Teacher.builder()
                .id(id)
                .member(member.toDomain())
                .detail(detail)
                .educations(education)
                .experiences(experience)
                .prizes(prize)
                .instagram(instagram)
                .youtube(youtube)
                .build();
    }

    public Teacher toDomainWithTeacherImage(List<TeacherImageJpaEntity> teacherImageJpaEntities) {
        return Teacher.builder()
                .id(id)
                .member(member.toDomain())
                .detail(detail)
                .educations(education)
                .experiences(experience)
                .prizes(prize)
                .instagram(instagram)
                .youtube(youtube)
                .imageUrls(teacherImageJpaEntities.stream().map(TeacherImageJpaEntity::getImageUrl).toList())
                .build();
    }

    public Teacher toDomainWithImageAndVideo(List<TeacherImageJpaEntity> teacherImageJpaEntities, List<TeacherVideoJpaEntity> teacherVideoJpaEntities) {
        return Teacher.builder()
                .id(id)
                .member(member.toDomain())
                .detail(detail)
                .educations(education)
                .experiences(experience)
                .prizes(prize)
                .instagram(instagram)
                .youtube(youtube)
                .imageUrls(teacherImageJpaEntities.stream().map(TeacherImageJpaEntity::getImageUrl).toList())
                .videoUrls(teacherVideoJpaEntities.stream().map(TeacherVideoJpaEntity::getVideoUrl).toList())
                .build();
    }

    public Teacher toDomainWithImageAndVideo(List<TeacherImageJpaEntity> teacherImageJpaEntities, List<TeacherVideoJpaEntity> teacherVideoJpaEntities, MemberJpaEntity memberJpaEntity) {
        return Teacher.builder()
                .id(id)
                .member(member.toDomain())
                .detail(detail)
                .member(memberJpaEntity.toDomain())
                .educations(education)
                .experiences(experience)
                .prizes(prize)
                .instagram(instagram)
                .youtube(youtube)
                .imageUrls(teacherImageJpaEntities.stream().map(TeacherImageJpaEntity::getImageUrl).toList())
                .videoUrls(teacherVideoJpaEntities.stream().map(TeacherVideoJpaEntity::getVideoUrl).toList())
                .build();
    }

    public void updateProfile(Teacher teacher) {
        this.detail = teacher.getDetail();
        this.education = teacher.getEducations();
        this.experience = teacher.getExperiences();
        this.prize = teacher.getPrizes();
        this.instagram = teacher.getInstagram();
        this.youtube = teacher.getYoutube();
    }
}
