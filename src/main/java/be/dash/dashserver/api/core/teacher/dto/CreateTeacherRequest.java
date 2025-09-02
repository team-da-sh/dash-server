package be.dash.dashserver.api.core.teacher.dto;

import java.util.List;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;
import be.dash.dashserver.core.domain.teacher.command.CreateTeacherCommand;

public record CreateTeacherRequest(
        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        @Size(max = 20, message = "닉네임은 최대 20자입니다.")
        String nickname,
        String instagram,
        String youtube,
        @NotNull(message = "학력은 null일 수 없습니다.")
        List<@NotBlank(message = "학력은 공백 불가.") @Size(max = 50, message = "학력 최대 50자") String> educations,
        @NotNull(message = "경력은 null일 수 없습니다.")
        List<@NotBlank(message = "경력은 공백 불가.") @Size(max = 50, message = "학력 최대 50자") String> experiences,
        @NotNull(message = "수상은 null일 수 없습니다.")
        List<@NotBlank(message = "수상은 공백 불가.") @Size(max = 50, message = "수상 최대 50자") String> prizes,
        @NotBlank(message = "소개는 공백일 수 없습니다.")
        @Size(min = 30, max = 500, message = "소개는 최소 30자, 최대 500자입니다.")
        String detail,
        @NotNull(message = "이미지 url 리스트는 null일 수 없습니다.")
        @Size(min = 1, message = "이미지 url은 최소 1개입니다.")
        List<String> imageUrls,
        @NotNull(message = "영상은 null일 수 없습니다.")
        @Size(max = 5, message = "영상은 최대 5개입니다.")
        List<String> videoUrls
) {

    @AssertTrue(message = "인스타그램과 유튜브 중 하나는 필수이며, 각 입력은 30자, 100자를 초과할 수 없습니다.")
    boolean isInstagramOrYoutubeValid() {
        boolean hasInstagram = StringUtils.hasText(instagram());
        boolean hasYoutube = StringUtils.hasText(youtube());

        boolean isLengthValid =
                (!hasInstagram || instagram().length() <= 30) &&
                        (!hasYoutube || youtube().length() <= 100);

        boolean isOnePresent = hasInstagram || hasYoutube;
        return isOnePresent && isLengthValid;
    }

    public CreateTeacherCommand toCommand(long memberId) {
        return new CreateTeacherCommand(memberId,
                nickname(),
                instagram(),
                youtube(),
                educations(),
                experiences(),
                prizes(),
                detail(),
                imageUrls(),
                videoUrls()
        );
    }
}
