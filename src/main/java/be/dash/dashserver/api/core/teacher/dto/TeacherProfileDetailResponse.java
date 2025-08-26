package be.dash.dashserver.api.core.teacher.dto;

import java.util.List;
import be.dash.dashserver.core.domain.teacher.service.dto.MyTeacherProfileDetailResult;

public record TeacherProfileDetailResponse(
        String nickname,
        String profileImage,
        String detail,
        String instagram,
        String youtube,
        List<String> educations,
        List<String> experiences,
        List<String> prizes,
        List<String> videoUrls
) {
    public static TeacherProfileDetailResponse from(MyTeacherProfileDetailResult result) {
        return new TeacherProfileDetailResponse(
                result.nickname(),
                result.profileImage(),
                result.detail(),
                result.instagram(),
                result.youtube(),
                result.educations(),
                result.experiences(),
                result.prizes(),
                result.videos()
        );
    }
}
