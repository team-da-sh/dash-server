package be.dash.dashserver.api.core.teacher.dto;

import java.util.List;
import be.dash.dashserver.api.core.member.dto.ApplyStatus;
import be.dash.dashserver.api.core.member.dto.MyLessonResponse;
import be.dash.dashserver.api.core.member.dto.StatusCount;
import be.dash.dashserver.core.domain.lesson.Lesson;

public record LessonStatusCountResponses(List<StatusCount> lessonsStatusCounts) {
    private static final String ALL_STATUS = "ALL";

    public static LessonStatusCountResponses from(List<Lesson> lessons) {
        List<MyLessonResponse> myLessons = lessons.stream().map(MyLessonResponse::from).toList();
        StatusCount all = new StatusCount(ALL_STATUS, myLessons.size());
        StatusCount applying = new StatusCount(ApplyStatus.APPLYING.name(), myLessons.stream()
                .filter(myLesson -> myLesson.applyStatus() == ApplyStatus.APPLYING).count());
        StatusCount finished = new StatusCount(ApplyStatus.FINISHED.name(), myLessons.stream()
                .filter(myLesson -> myLesson.applyStatus() == ApplyStatus.FINISHED).count());
        return new LessonStatusCountResponses(List.of(all, applying, finished));
    }
}
