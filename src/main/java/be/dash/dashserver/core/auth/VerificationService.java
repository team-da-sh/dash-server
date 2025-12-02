package be.dash.dashserver.core.auth;

import java.security.SecureRandom;
import java.util.Objects;
import org.springframework.stereotype.Service;
import be.dash.dashserver.core.auth.command.PhoneVerificationApprovalCommand;
import be.dash.dashserver.core.auth.command.PhoneVerificationCommand;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.exception.ConflictException;
import be.dash.dashserver.core.exception.VerificationException;
import be.dash.dashserver.core.external.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationService {
    private final PhoneVerificationRepository phoneVerificationRepository;
    private final MessageSender messageSender;
    private final PhoneVerificationQuotaRepository phoneVerificationQuotaRepository;
    private final MemberRepository memberRepository;

    public void requestPhoneVerification(PhoneVerificationCommand command) {
        long memberId = command.memberId();
        String phoneNumber = command.phoneNumber();

        if (!phoneVerificationQuotaRepository.tryConsumeDailyQuota(memberId)) {
            throw VerificationException.exceedsRequestLimit();
        }
        validatePhoneNumber(memberId, phoneNumber);

        String code = generateCode();
        phoneVerificationRepository.saveCode(memberId, phoneNumber, code);
        messageSender.sendVerification(phoneNumber, code);

        log.info("발송 완료: memberId={}, phone={}, code={}", memberId, phoneNumber, code);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private void validatePhoneNumber(long id, String phoneNumber) {
        if (memberRepository.existsByPhoneNumber(id, phoneNumber)) {
            throw new ConflictException("이미 사용 중인 전화번호입니다.");
        }
    }

    public boolean verifyPhone(PhoneVerificationApprovalCommand command) {

        long memberId = command.memberId();
        String phoneNumber = command.phoneNumber();
        String savedCode = phoneVerificationRepository.getCode(memberId, phoneNumber);

        if (Objects.isNull(savedCode) || !savedCode.equals(command.code())) {
            return false;
        }
        phoneVerificationRepository.removeCode(memberId, phoneNumber);
        return true;
    }
}
