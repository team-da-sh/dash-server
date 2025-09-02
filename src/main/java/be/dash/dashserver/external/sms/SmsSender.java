package be.dash.dashserver.external.sms;

import org.springframework.stereotype.Component;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import be.dash.dashserver.core.external.MessageSender;
import be.dash.dashserver.external.config.sms.SolProperties;
import lombok.RequiredArgsConstructor;

@Component
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
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

    }
}
