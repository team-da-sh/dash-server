package be.dash.dashserver.external.s3;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import be.dash.dashserver.core.exception.ImageStorageException;
import be.dash.dashserver.core.image.ImageReader;
import be.dash.dashserver.external.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ImageReader implements ImageReader {
    private final S3Properties s3Properties;
    private final S3Client s3Client;

    @Override
    public List<String> getAllKeys() {
        try {
            return performGetAllKeys();
        } catch (AwsServiceException e) {
            log.error("S3 키 목록 조회 실패 - 상태코드 : {}, 에러메시지 : {}", e.statusCode(), e.awsErrorDetails().errorMessage());
            throw new ImageStorageException("키 조회에 실패했습니다.(서비스 오류)");
        } catch (SdkClientException e) {
            log.error("S3 키 목록 조회 실패 - 에러메시지 : {}", e.getMessage());
            throw new ImageStorageException("키 조회에 실패했습니다.(내부 네트워크 오류)");
        }
    }

    private List<String> performGetAllKeys() {;
        ListObjectsV2Request request = buildRequest();
        List<String> keys = new ArrayList<>();
        ListObjectsV2Response response;
        do {
            response = s3Client.listObjectsV2(request);
            keys.addAll(
                    response.contents()
                            .stream()
                            .map(S3Object::key)
                            .toList()
            );
            request = request.toBuilder()
                    .continuationToken(response.nextContinuationToken())
                    .build();
        } while (response.isTruncated());
        return keys;
    }

    private ListObjectsV2Request buildRequest() {
        return ListObjectsV2Request.builder()
                .bucket(s3Properties.s3BucketName())
                .build();
    }
}
