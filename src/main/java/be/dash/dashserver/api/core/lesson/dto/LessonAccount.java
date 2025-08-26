package be.dash.dashserver.api.core.lesson.dto;

import be.dash.dashserver.core.domain.account.Account;
import be.dash.dashserver.core.domain.lesson.Lesson;

public record LessonAccount(Lesson lesson, Account account) {
}
