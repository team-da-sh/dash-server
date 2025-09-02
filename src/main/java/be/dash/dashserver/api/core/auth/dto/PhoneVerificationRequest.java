package be.dash.dashserver.api.core.auth.dto;

import jakarta.validation.constraints.Size;

public record PhoneVerificationRequest(
        @Size(min = 11, max = 11, message = "휴대폰 번호는 11자리여야 합니다.")
        String phoneNumber
) {
}
