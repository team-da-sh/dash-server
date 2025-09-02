package be.dash.dashserver.database.core.teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.core.domain.teacher.Teachers;
import be.dash.dashserver.core.domain.teacher.service.TeacherRepository;
import be.dash.dashserver.core.exception.NotFoundException;
import be.dash.dashserver.database.core.teacher.projection.TeacherLessonCount;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TeacherRepositoryAdapter implements TeacherRepository {
    private final TeacherJpaRepository teacherJpaRepository;
    private final TeacherImageJpaRepository teacherImageJpaRepository;
    private final TeacherVideoJpaRepository teacherVideoJpaRepository;

    @Override
    public void save(Teacher teacher) {
        TeacherJpaEntity teacherJpaEntity = TeacherJpaEntity.fromDomain(teacher);
        teacherJpaRepository.save(teacherJpaEntity);
    }

    @Override
    public Teachers findTeachersSortByLessonCountsDesc(String keyword) {
        List<Teacher> teachers = new ArrayList<>();
        List<TeacherLessonCount> teacherLessonCounts = teacherJpaRepository.findTeacherLessonCountsDesc(keyword);
        teacherLessonCounts.forEach(teacherLessonCount -> {
            List<String> teacherImages = teacherImageJpaRepository.findAllByTeacherId(teacherLessonCount.teacherId())
                    .stream().map(TeacherImageJpaEntity::getImageUrl).toList();
            Teacher teacher = teacherLessonCount.toDomain(teacherImages);
            teachers.add(teacher);
        });
        return new Teachers(teachers);
    }

    @Override
    public void register(Teacher teacher) {
        TeacherJpaEntity teacherJpaEntity = TeacherJpaEntity.fromDomain(teacher);
        teacherJpaRepository.save(teacherJpaEntity);

        List<TeacherImageJpaEntity> teacherImageJpaEntities = teacher.getImageUrls().stream()
                .map(imageUrl -> TeacherImageJpaEntity.builder()
                        .teacherId(teacherJpaEntity.getId())
                        .imageUrl(imageUrl)
                        .build()).toList();
        teacherImageJpaRepository.saveAll(teacherImageJpaEntities);

        List<TeacherVideoJpaEntity> teacherVideoJpaEntities = teacher.getVideoUrls().stream()
                .map(videoUrl -> TeacherVideoJpaEntity.builder()
                        .teacherId(teacherJpaEntity.getId())
                        .videoUrl(videoUrl)
                        .build()).toList();
        teacherVideoJpaRepository.saveAll(teacherVideoJpaEntities);
    }

    @Override
    public Optional<Teacher> findByMemberId(Long memberId) {
        return teacherJpaRepository.findByMemberId(memberId)
                .map(TeacherJpaEntity::toDomain);
    }

    @Override
    public Teacher findByTeacherId(Long teacherId) {
        TeacherJpaEntity teacherJpaEntity = teacherJpaRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("해당하는 선생님을 찾을 수 없습니다."));
        List<TeacherVideoJpaEntity> videos = teacherVideoJpaRepository.findAllByTeacherId(teacherId);
        List<TeacherImageJpaEntity> images = teacherImageJpaRepository.findAllByTeacherId(teacherId);
        return teacherJpaEntity.toDomainWithImageAndVideo(images, videos);
    }

    @Override
    public Optional<Teacher> update(Teacher teacher, long memberId) {
        return teacherJpaRepository.findByMemberId(memberId)
                .map(entity -> {
                    entity.updateProfile(teacher);
                    return entity.toDomain();
                });
    }

    @Override
    public boolean existByInstagram(String instagram) {
        return teacherJpaRepository.existsByInstagram(instagram);
    }

    @Override
    public boolean existByYoutube(String youtube) {
        return teacherJpaRepository.existsByYoutube(youtube);
    }

    @Override
    public boolean existByYoutubeAndMemberIdNot(String youtube, long memberId) {
        return teacherJpaRepository.existsByYoutubeAndMemberIdNot(youtube, memberId);
    }

    @Override
    public boolean existByInstagramAndMemberIdNot(String instagram, long memberId) {
        return teacherJpaRepository.existsByInstagramAndMemberIdNot(instagram, memberId);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return teacherJpaRepository.existsByNickname(nickname);
    }
}
