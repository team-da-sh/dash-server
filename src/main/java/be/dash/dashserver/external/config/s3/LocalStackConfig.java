package be.dash.dashserver.external.config.s3;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Configuration
@Profile({"local", "test"})
@RequiredArgsConstructor
@EnableConfigurationProperties(S3Properties.class)
public class LocalStackConfig {

    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:3.5.0");

    private final S3Properties s3Properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public LocalStackContainer localStackContainer() {
        return new LocalStackContainer(LOCALSTACK_IMAGE)
                .withServices(S3);
    }

    @Bean
    public S3Client s3Client(LocalStackContainer localStackContainer) {
        S3Client client = S3Client.builder()
                .endpointOverride(localStackContainer.getEndpoint())
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localStackContainer.getAccessKey(), localStackContainer.getSecretKey())
                        )
                )
                .region(Region.of(localStackContainer.getRegion()))
                .build();
        client.createBucket(b -> b.bucket(s3Properties.s3BucketName()));
        return client;
    }
}
