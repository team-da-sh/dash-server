package be.dash.dashserver.api.core.lesson.dto;

public record LessonAccountResponse(
        String depositor,
        String bankName,
        String accountNumber,
        long price
) {
    public LessonAccountResponse(LessonAccount lessonAccount) {
        this(
                lessonAccount.account().depositor(),
                lessonAccount.account().bankName(),
                lessonAccount.account().accountNumber(),
                lessonAccount.lesson().getPrice()
        );
    }
}
