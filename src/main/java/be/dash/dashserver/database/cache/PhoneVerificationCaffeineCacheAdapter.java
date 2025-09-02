package be.dash.dashserver.database.cache;

import org.springframework.stereotype.Component;
import com.github.benmanes.caffeine.cache.Cache;
import be.dash.dashserver.core.auth.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhoneVerificationCaffeineCacheAdapter implements PhoneVerificationRepository {

    private final Cache<String, String> phoneVerificationCache;

    @Override
    public void saveCode(long memberId, String phoneNumber, String code) {
        String key = getKey(memberId, phoneNumber);
        phoneVerificationCache.put(key, code);
    }

    @Override
    public String getCode(long memberId, String phoneNumber) {
        String key = getKey(memberId, phoneNumber);
        return phoneVerificationCache.getIfPresent(key);
    }

    @Override
    public void removeCode(long memberId, String phoneNumber) {
        String key = getKey(memberId, phoneNumber);
        phoneVerificationCache.invalidate(key);
    }

    private String getKey(long memberId, String phoneNumber) {
        return memberId + ":" + phoneNumber;

    }
}
