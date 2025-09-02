package be.dash.dashserver.api.core.teacher.dto;

public record NicknameValidationResponse (boolean isDuplicated) {
    public static NicknameValidationResponse from(boolean isDuplicated) {
        return new NicknameValidationResponse(isDuplicated);
    }

}
