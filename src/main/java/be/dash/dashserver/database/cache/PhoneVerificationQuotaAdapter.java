package be.dash.dashserver.database.cache;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;
import com.github.benmanes.caffeine.cache.Cache;
import be.dash.dashserver.core.auth.PhoneVerificationQuotaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhoneVerificationQuotaAdapter implements PhoneVerificationQuotaRepository {

    private final Cache<String, Integer> phoneVerificationDailyCountCache;
    private static final int DAILY_LIMIT = 5;

    @Override
    public boolean tryConsumeDailyQuota(long memberId) {
        String key = todayKey(memberId);
        ConcurrentMap<String, Integer> map = phoneVerificationDailyCountCache.asMap();

        int after = map.compute(key, (k, v) -> {
            int cur;

            if (v == null) {
                cur = 0;
            } else {
                cur = v;
            }

            return cur + 1;
        });
        return after <= DAILY_LIMIT;
    }

    @Override
    public int getRemainingDailyQuota(long memberId) {
        String key = todayKey(memberId);
        Integer cur = phoneVerificationDailyCountCache.getIfPresent(key);
        int used;
        if (cur == null) {
            used = 0;
        } else {
            used = cur;
        }
        return Math.max(0, DAILY_LIMIT - used);
    }

    @Override
    public void resetDailyQuota(long memberId) {
        String key = todayKey(memberId);
        phoneVerificationDailyCountCache.invalidate(key);
    }

    private static String todayKey(long memberId) {
        String ymd = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.BASIC_ISO_DATE);
        return "verif:req:" + ymd + ":" + memberId;
    }
}
