package be.dash.dashserver.external.s3;

import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import be.dash.dashserver.core.exception.ImageStorageException;
import be.dash.dashserver.core.image.ImageUploader;
import be.dash.dashserver.external.config.s3.S3Config;
import be.dash.dashserver.external.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ImageUploader implements ImageUploader {
    private static final String IMAGE_EXTENSION = ".jpg";
    private static final String CONTENT_DISPOSITION = "inline";

    private final S3Properties s3Properties;
    private final S3Config s3Config;

    @Override
    public String upload(MultipartFile image) {
        final String key = generateImageFileName();
        PutObjectRequest request = buildRequest(image, key);
        RequestBody requestBody = toRequestBody(image);
        performUpload(request, requestBody);
        return s3Properties.s3Endpoint() + key;
    }

    private String generateImageFileName() {
        return UUID.randomUUID() + IMAGE_EXTENSION;
    }

    private PutObjectRequest buildRequest(MultipartFile image, String key) {
        return PutObjectRequest.builder()
                .bucket(s3Properties.s3BucketName())
                .key(key)
                .contentType(image.getContentType())
                .contentDisposition(CONTENT_DISPOSITION)
                .build();
    }

    private static RequestBody toRequestBody(MultipartFile image) {
        RequestBody requestBody;
        try {
            requestBody = RequestBody.fromBytes(image.getBytes());
        } catch (IOException e) {
            throw new ImageStorageException("이미지 저장에 실패했습니다.");
        }
        return requestBody;
    }

    private void performUpload(PutObjectRequest request, RequestBody requestBody) {
        try {
            s3Config.getS3Client().putObject(request, requestBody);
        } catch (AwsServiceException e) {
            log.error("S3 업로드 실패 - 상태코드 : {}, 에러메시지 : {}", e.statusCode(), e.awsErrorDetails().errorMessage());
            throw new ImageStorageException("이미지 저장에 실패했습니다.(서비스 오류)");
        } catch (SdkClientException e) {
            log.error("S3 업로드 실패 - 에러메시지 : {}", e.getMessage());
            throw new ImageStorageException("이미지 저장에 실패했습니다.(내부 네트워크 오류)");
        }
    }
}
