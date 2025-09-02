package be.dash.dashserver.api.core.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import be.dash.dashserver.core.domain.member.command.OnboardCommand;

public record OnBoardRequest(
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        @Size(min = 2, max = 8, message = "이름은 최소 2자, 최대 8자입니다.")
        String name,
        @NotBlank(message = "전화번호는 공백일 수 없습니다.")
        @Size(min = 11, max = 11, message = "전화번호는 11자입니다.")
        String phoneNumber
) {
    public OnboardCommand toCommand(long memberId) {
        return new OnboardCommand(memberId, name, phoneNumber);
    }
}

