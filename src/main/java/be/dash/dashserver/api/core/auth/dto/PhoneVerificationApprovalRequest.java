package be.dash.dashserver.api.core.auth.dto;

import jakarta.validation.constraints.Size;

public record PhoneVerificationApprovalRequest(
        @Size(min = 11, max = 11)
        String phoneNumber,
        @Size(min = 6, max = 6)
        String code
) {
}
