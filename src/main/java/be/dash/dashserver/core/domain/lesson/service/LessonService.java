package be.dash.dashserver.core.domain.lesson.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Keyword;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.LessonSortOption;
import be.dash.dashserver.core.domain.lesson.Lessons;
import be.dash.dashserver.core.domain.lesson.Round;
import be.dash.dashserver.core.domain.lesson.command.CreateLessonCommand;
import be.dash.dashserver.core.domain.lesson.command.UpdateLessonCommand;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.core.domain.teacher.service.TeacherRepository;
import be.dash.dashserver.core.exception.NotFoundException;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final LessonImageRepository lessonImageRepository;
    private final LessonRoundRepository lessonRoundRepository;

    public Lessons search(Genre genre, Level level, LocalDateTime startDateTime, LocalDateTime endDateTime, Keyword keyword, LessonSortOption sortOption) {
        Lessons lessons = new Lessons(
                lessonRepository.findActiveLessonsByFilters(genre, level, startDateTime, endDateTime, keyword.getValue(), LocalDateTime.now())
        );
        return lessons.sort(sortOption);
    }

    @Transactional
    public void createLesson(CreateLessonCommand command) {
        Teacher teacher = teacherRepository.findByMemberId(command.memberId())
                .orElseThrow(() -> new NotFoundException("해당하는 선생님을 찾을 수 없습니다."));

        Lesson lesson = command.toDomainWith(teacher);
        lessonRepository.save(lesson);
    }

    public List<Genre> getPopularGenres() {
        return lessonRepository.popularGenres(LocalDateTime.now());
    }

    public Lessons searchBySortOption(LessonSortOption sortOption) {
        List<Lesson> activeLessons = lessonRepository.findActiveLessons(LocalDateTime.now())
                .stream()
                .toList();
        Lessons lessons = new Lessons(activeLessons);
        Lessons sortedLessons = lessons.sort(sortOption);
        return new Lessons(sortedLessons.lessons().stream()
                .limit(15)
                .toList());
    }

    public Lesson findById(long lessonId) {
        return lessonRepository.findLessonsById(lessonId);
    }

    @Transactional
    public void update(UpdateLessonCommand command) {
        lessonRepository.update(command.toDomain(), command.lessonId());
        lessonRoundRepository.replace(command.lessonId(),
                command.times().stream().map(round -> new Round(round.startTime(), round.endTime())).toList());
        lessonImageRepository.replace(command.lessonId(), command.imageUrls());
    }
}
