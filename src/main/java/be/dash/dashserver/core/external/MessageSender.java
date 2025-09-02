package be.dash.dashserver.core.external;

public interface MessageSender {
    void sendVerification(String to, String content);
}
