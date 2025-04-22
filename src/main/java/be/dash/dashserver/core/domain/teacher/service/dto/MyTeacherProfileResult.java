package be.dash.dashserver.core.domain.teacher.service.dto;

import be.dash.dashserver.core.domain.teacher.Teacher;

public record MyTeacherProfileResult(String profileImage,
                                     String nickname,
                                     String instagram,
                                     String youtube) {
    public static MyTeacherProfileResult of(String profileImage,
                                            String nickname,
                                            Teacher teacher) {
        return new MyTeacherProfileResult(profileImage, nickname, teacher.getInstagram(), teacher.getYoutube());
    }
}
