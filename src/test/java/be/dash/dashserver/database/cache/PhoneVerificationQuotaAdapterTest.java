package be.dash.dashserver.database.cache;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class PhoneVerificationQuotaAdapterTest {

    @Autowired
    private PhoneVerificationQuotaAdapter phoneVerificationQuotaAdapter;

    @BeforeEach
    void setUp() {
        phoneVerificationQuotaAdapter.resetDailyQuota(1L);
    }

    @Test
    @DisplayName("한도이하 시도하면 성공한다.")
    void tryConsumeDailyQuota() {
        for (int i = 0; i < 5; i++) {
            boolean allowed = phoneVerificationQuotaAdapter.tryConsumeDailyQuota(1L);
            Assertions.assertThat(allowed).isTrue();
        }
    }

    @Test
    @DisplayName("한도이상 시도하면 실패한다.")
    void failTryConsumeDailyQuota() {
        boolean allowed;
        for (int i = 0; i < 5; i++) {
            allowed = phoneVerificationQuotaAdapter.tryConsumeDailyQuota(1L);
            Assertions.assertThat(allowed).isTrue();
        }
        allowed = phoneVerificationQuotaAdapter.tryConsumeDailyQuota(1L);
        Assertions.assertThat(allowed).isFalse();
    }

    @Test
    @DisplayName("잔여 쿼터를 정확히 반환한다.")
    void getRemainingQuota() {
        boolean allowed = phoneVerificationQuotaAdapter.tryConsumeDailyQuota(1L);
        int remainingQuota = phoneVerificationQuotaAdapter.getRemainingDailyQuota(1L);
        Assertions.assertThat(remainingQuota).isEqualTo(4);
    }
}