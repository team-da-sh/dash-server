package be.dash.dashserver.api.core.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import be.dash.dashserver.core.domain.member.command.OnboardCommand;

public record OnBoardRequest(
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        @Size(min = 2, max = 8, message = "이름은 최소 2자, 최대 8자입니다.")
        String name,
        @NotBlank(message = "전화번호는 공백일 수 없습니다.")
        @Size(min = 11, max = 11, message = "전화번호는 11자입니다.")
        String phoneNumber,
        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        @Size(max = 8, message = "댄서네임은 최대 8자입니다.")
        String nickname,
        String profileImageUrl
) {
    @AssertTrue(message = "이미지 url은 null이거나 공백이 아니어야 합니다.")
    public boolean isProfileImageUrlValid() {
        return profileImageUrl == null || !profileImageUrl.isBlank();
    }

    public OnboardCommand toCommand(long memberId) {
            return new OnboardCommand(memberId, name, phoneNumber, nickname, profileImageUrl);
    }
}

