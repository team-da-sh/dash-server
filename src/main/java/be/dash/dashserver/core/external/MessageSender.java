package be.dash.dashserver.core.external;

public interface MessageSender {

    void sendVerification(String to, String content);

    void sendClassApply(String to, String instructorName, String className, String studentName, String studentPhone);

    void sendClassConfirmed(String to, String studentName, String instructorName, String className);

    void sendCancelledBySystem(String to, String studentName, String instructorName, String className);

    void sendCancelledByStudent(String to, String instructorName, String className, String studentName, String studentPhone, String bankName, String refundAccount);

    void sendCancelDone(String to, String studentName, String instructorName, String className);
}
