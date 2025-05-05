package be.dash.dashserver.external.config.s3;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("develop")
@RequiredArgsConstructor
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

    private static final String AWS_ACCESS_KEY_ID = "aws.accessKeyId";
    private static final String AWS_SECRET_ACCESS_KEY = "aws.secretAccessKey";
    private final S3Properties s3Properties;

    @Bean
    public SystemPropertyCredentialsProvider systemPropertyCredentialsProvider() {
        System.setProperty(AWS_ACCESS_KEY_ID, s3Properties.accessKey());
        System.setProperty(AWS_SECRET_ACCESS_KEY, s3Properties.secretKey());
        return SystemPropertyCredentialsProvider.create();
    }

    @Bean
    public Region getRegion() {
        return Region.of(s3Properties.region());
    }

    @Bean
    public S3Client getS3Client() {
        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(Duration.ofSeconds(3))
                .apiCallTimeout(Duration.ofSeconds(14))
                .retryPolicy(
                        RetryPolicy.builder()
                                .numRetries(3)
                                .retryCondition(RetryCondition.defaultRetryCondition())
                                .backoffStrategy(BackoffStrategy.defaultStrategy())
                                .build()
                )
                .build();
        return S3Client.builder()
                .region(getRegion())
                .credentialsProvider(systemPropertyCredentialsProvider())
                .overrideConfiguration(overrideConfig)
                .build();
    }
}
