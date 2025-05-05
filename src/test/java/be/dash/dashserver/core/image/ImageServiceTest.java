package be.dash.dashserver.core.image;

import java.nio.charset.StandardCharsets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mock.web.MockMultipartFile;
import be.dash.dashserver.ServiceSliceTest;
import be.dash.dashserver.database.core.member.MemberJpaEntity;
import be.dash.dashserver.database.core.member.MemberJpaRepository;
import be.dash.dashserver.database.fixture.MemberJpaEntityFixture;
import be.dash.dashserver.external.config.s3.S3Properties;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

//@EnableConfigurationProperties(S3Properties.class)
class ImageServiceTest extends ServiceSliceTest {

    @Autowired
    private ImageService imageService;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Properties s3Properties;
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("이미지를 업도드하면 S3에 저장된다.")
    void upload() {
        // given
        MockMultipartFile file = createMultipartFile("testImage");
        // when
        String url = imageService.upload(file);

        // then
        ResponseBytes<GetObjectResponse> object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(s3Properties.s3BucketName())
                        .key(url.substring(url.lastIndexOf("/") + 1))
                        .build(),
                ResponseTransformer.toBytes()
        );
        String downloaded = new String(object.asByteArray(), StandardCharsets.UTF_8);
        Assertions.assertThat(downloaded).isEqualTo("testImage");
    }

    @Test
    @DisplayName("사용자의 프로필에 사용하지 않는 이미지를 삭제한다.")
    void cleanUpUnusedProfileImages() {
        // given
        String url = imageService.upload(createMultipartFile("testImage")); // dangling
        MemberJpaEntity member = MemberJpaEntityFixture.create();
        memberJpaRepository.save(member);

        // when
        imageService.cleanUpUnusedProfileImages();

        // then
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(s3Properties.s3BucketName())
                .build());

        Assertions.assertThat(listObjectsV2Response.contents().size()).isEqualTo(0);
    }


    private MockMultipartFile createMultipartFile(String imageKey) {
        return new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                imageKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}
