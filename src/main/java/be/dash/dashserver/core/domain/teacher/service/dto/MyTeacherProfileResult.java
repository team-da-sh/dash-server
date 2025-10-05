package be.dash.dashserver.core.domain.teacher.service.dto;

import be.dash.dashserver.core.domain.account.Account;
import be.dash.dashserver.core.domain.teacher.Teacher;

public record MyTeacherProfileResult(String profileImage,
                                     String nickname,
                                     String instagram,
                                     String youtube,
                                     Boolean accountRegistered
                                     ) {
    public static MyTeacherProfileResult of(String profileImage,
                                            Teacher teacher,
                                            Account account) {
        return new MyTeacherProfileResult(profileImage, teacher.getNickname(), teacher.getInstagram(), teacher.getYoutube(), account.isRegistered());
    }
}
