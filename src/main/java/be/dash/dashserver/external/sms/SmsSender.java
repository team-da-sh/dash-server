package be.dash.dashserver.external.sms;

import org.springframework.stereotype.Component;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import be.dash.dashserver.core.exception.SmsException;
import be.dash.dashserver.core.external.MessageSender;
import be.dash.dashserver.external.config.sms.SolProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmsSender implements MessageSender {

    private final DefaultMessageService messageService;
    private final SolProperties properties;

    @Override
    public void sendVerification(String to, String number) {
        String text = String.format(properties.verificationTemplate(), number);
        sendMessage(to, text);
    }

    public void sendClassApply(String to, String instructorName, String className, String studentName, String studentPhone) {
        String template = properties.templates().get("class-apply");
        String text = String.format(template, instructorName, className, studentName, studentPhone);
        sendMessage(to, text);
    }

    public void sendClassConfirmed(String to, String studentName, String instructorName, String className) {
        String template = properties.templates().get("class-confirmed");
        String text = String.format(template, studentName, instructorName, className);
        sendMessage(to, text);
    }

    public void sendCancelledBySystem(String to, String studentName, String instructorName, String className) {
        String template = properties.templates().get("class-cancelled-by-system");
        String text = String.format(template, studentName, instructorName, className);
        sendMessage(to, text);
    }

    public void sendCancelledByStudent(String to, String instructorName, String className, String studentName, String studentPhone, String bankName, String refundAccount) {
        String template = properties.templates().get("class-cancelled-by-student");
        String text = String.format(template, instructorName, className, studentName, studentPhone, bankName, refundAccount);
        sendMessage(to, text);
    }

    public void sendCancelDone(String to, String studentName, String instructorName, String className) {
        String template = properties.templates().get("class-cancel-done");
        String text = String.format(template, studentName, instructorName, className);
        sendMessage(to, text);
    }

    private void sendMessage(String to, String text) {
        Message message = new Message();
        message.setFrom(properties.from());
        message.setTo(to);
        message.setText(text);

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.error("Business SMS send failure: {}", e.getMessage());
            throw SmsException.failBusiness();
        } catch (Exception e) {
            log.error("Transient SMS send failure: {}", e.getMessage());
            throw SmsException.failTransient();
        }
    }
}
