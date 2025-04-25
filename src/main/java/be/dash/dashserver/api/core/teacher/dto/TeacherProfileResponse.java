package be.dash.dashserver.api.core.teacher.dto;

import be.dash.dashserver.core.domain.teacher.service.dto.MyTeacherProfileResult;

public record TeacherProfileResponse(String profileImage,
                                     String nickname,
                                     String instagram,
                                     String youtube) {
    public static TeacherProfileResponse from(MyTeacherProfileResult myTeacherProfileResult) {
        return new TeacherProfileResponse(
                myTeacherProfileResult.profileImage(),
                myTeacherProfileResult.nickname(),
                myTeacherProfileResult.instagram(),
                myTeacherProfileResult.youtube()
        );
    }
}
