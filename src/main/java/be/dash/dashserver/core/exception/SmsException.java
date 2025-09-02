package be.dash.dashserver.core.exception;

import lombok.Getter;

@Getter
public class SmsException extends DashException {
    public enum Reason {
        TRANSIENT,
        BUSINESS
    }
    private final Reason reason;

    public SmsException(String s, Reason reason) {
        super(s);
        this.reason = reason;
    }

    public static SmsException failBusiness() {
        return new SmsException("SMS 전송 실패", Reason.BUSINESS);
    }
    public static SmsException failTransient() {
        return new SmsException("SMS 전송 실패", Reason.TRANSIENT);
    }
}
