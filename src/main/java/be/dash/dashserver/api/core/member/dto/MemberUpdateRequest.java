package be.dash.dashserver.api.core.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import be.dash.dashserver.core.domain.member.command.MemberUpdateCommand;

public record MemberUpdateRequest(
        @NotBlank @Size(min = 2, max = 8) String name,
        @Size(min = 11, max = 11) @NotBlank String phoneNumber,
        @NotBlank @Size(max = 10) String nickname,
        String profileImageUrl
) {
    @AssertTrue(message = "profileImageUrl이 null이 아니면 공백일 수 없습니다.")
    public boolean isProfileImageUrlValid() {
        return profileImageUrl == null || !profileImageUrl.isBlank();
    }

    public MemberUpdateCommand toCommand(Long memberId) {
        return new MemberUpdateCommand(memberId, name, phoneNumber, nickname, profileImageUrl);
    }
}
