package be.dash.dashserver.core.domain.teacher.service.dto;

import java.util.List;
import be.dash.dashserver.core.domain.teacher.Teacher;

public record MyTeacherProfileDetailResult(
        String nickname,
        String profileImage,
        String detail,
        String instagram,
        String youtube,
        List<String> educations,
        List<String> experiences,
        List<String> prizes,
        List<String> videos
) {
    public static MyTeacherProfileDetailResult of(
            Teacher teacher,
            String image,
            List<String> videos
    ) {
        return new MyTeacherProfileDetailResult(
                teacher.getNickname(),
                image,
                teacher.getDetail(),
                teacher.getInstagram(),
                teacher.getYoutube(),
                teacher.getEducations(),
                teacher.getExperiences(),
                teacher.getPrizes(),
                videos
        );
    }
}
