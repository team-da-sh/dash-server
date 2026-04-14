package be.dash.dashserver.api.core.lesson.dto;

public record LessonCreateResponse(long id) {
        public LessonCreateResponse(long id) {
            this.id = id;
        }
}
