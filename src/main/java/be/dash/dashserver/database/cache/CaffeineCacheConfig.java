package be.dash.dashserver.database.cache;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Cache<String, String> phoneVerificationCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .maximumSize(10000)
                .build();
    }

    @Bean
    public Cache<String, Integer> phoneVerificationDailyCountCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofDays(1))
                .maximumSize(10000)
                .build();
    }

}
