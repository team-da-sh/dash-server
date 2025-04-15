package be.dash.dashserver.api.core.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OnBoardRequest(
        @NotBlank @Size(min = 2, max = 8) String name,
        @Size(min = 11, max = 11) @NotBlank String phoneNumber,
        @NotBlank @Size(max = 10) String nickname,
        @NotBlank String profileImageUrl
) {
}
