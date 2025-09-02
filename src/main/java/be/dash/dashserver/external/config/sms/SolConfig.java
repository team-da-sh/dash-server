package be.dash.dashserver.external.config.sms;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConfigurationPropertiesScan(basePackages = "be.dash.dashserver.external.config.sms")
public class SolConfig {

    private final SolProperties solProperties;

    @Bean
    public DefaultMessageService solapiMessageService() {
        return NurigoApp.INSTANCE.initialize(
            solProperties.apiKey(),
            solProperties.apiSecret(),
            solProperties.baseUrl()
        );
    }
}
