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
        Message message = new Message();
            message.setFrom(properties.from());
            message.setTo(to);
            message.setText(String.format(properties.verificationTemplate(), number));
        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            throw SmsException.failBusiness();
        } catch (Exception exception) {
            throw SmsException.failTransient();
        }
    }
}
