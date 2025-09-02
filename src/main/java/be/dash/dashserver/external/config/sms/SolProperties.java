package be.dash.dashserver.external.config.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solapi")
public record SolProperties(String apiKey,
                            String apiSecret,
                            String baseUrl,
                            String from,
                            String verificationTemplate) {
}
