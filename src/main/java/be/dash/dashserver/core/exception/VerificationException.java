package be.dash.dashserver.core.exception;

public class VerificationException extends DashException {
    public VerificationException(String s) {
        super(s);
    }

    public static VerificationException exceedsRequestLimit() {
        return new VerificationException("요청 한도 초과(하루에 5회)");
    }
}
