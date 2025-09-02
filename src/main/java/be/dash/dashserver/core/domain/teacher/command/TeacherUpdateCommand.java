package be.dash.dashserver.core.domain.teacher.command;

import java.util.List;
import be.dash.dashserver.core.domain.teacher.Teacher;

public record TeacherUpdateCommand (
        long memberId,
        String nickname,
        String detail,
        List<String> imageUrls,
        String instagram,
        String youtube,
        List<String> educations,
        List<String> experiences,
        List<String> prizes,
        List<String> videoUrls
) {
    public Teacher toTeacher() {
        return Teacher.builder()
                .detail(detail)
                .nickname(nickname)
                .instagram(instagram)
                .youtube(youtube)
                .educations(educations)
                .experiences(experiences)
                .prizes(prizes)
                .videoUrls(videoUrls)
                .build();
    }
}
