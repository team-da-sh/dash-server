package be.dash.dashserver.core.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import be.dash.dashserver.core.auth.command.PhoneVerificationCommand;
import be.dash.dashserver.core.external.MessageSender;

@SpringBootTest
@ActiveProfiles("test")
class VerificationServiceTest {

    @Autowired
    private VerificationService verificationService;
    @MockitoBean
    private MessageSender messageSender;
    @Autowired
    private PhoneVerificationQuotaRepository quotaRepository;
    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;

    @Test
    @DisplayName("휴대폰 인증 요청을 처리하고 인증 코드를 생성 및 저장한다.")
    void requestPhoneVerification() {
        verificationService.requestPhoneVerification(new PhoneVerificationCommand(1L, "01012345678"));
        String code = phoneVerificationRepository.getCode(1L, "01012345678");
        Assertions.assertThat(code).isNotNull();
        Assertions.assertThat(quotaRepository.getRemainingDailyQuota(1L)).isEqualTo(4);
    }
}
