package be.dash.dashserver.external.s3;

import java.util.List;
import org.springframework.stereotype.Component;
import be.dash.dashserver.core.exception.ImageStorageException;
import be.dash.dashserver.core.image.ImageDeleter;
import be.dash.dashserver.external.config.s3.S3Config;
import be.dash.dashserver.external.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ImageDeleter implements ImageDeleter {
    private final S3Properties s3Properties;
    private final S3Config s3Config;

    @Override
    public void deleteAllByKeys(List<String> keysToDelete) {
        if (keysToDelete.isEmpty()) {
            return;
        }
        DeleteObjectsRequest request = buildRequest(keysToDelete);
        DeleteObjectsResponse response = performDeleteAllByKeys(request);
        handlePartialDeleteErrors(response);
    }

    private DeleteObjectsResponse performDeleteAllByKeys(DeleteObjectsRequest deleteObjectRequest) {
        try {
            return s3Config.getS3Client().deleteObjects(deleteObjectRequest);
        } catch (AwsServiceException e) {
            log.error("S3 삭제 실패 - 상태코드 : {}, 에러메시지 : {}", e.statusCode(), e.awsErrorDetails().errorMessage());
            throw new ImageStorageException("이미지 삭제에 실패했습니다.(서비스 오류)");
        } catch (SdkClientException e) {
            log.error("S3 삭제 실패 - 에러메시지 : {}", e.getMessage());
            throw new ImageStorageException("이미지 삭제에 실패했습니다.(내부 네트워크 오류)");
        }
    }

    private DeleteObjectsRequest buildRequest(List<String> keysToDelete) {
        List<ObjectIdentifier> list = keysToDelete.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build()).toList();
        return DeleteObjectsRequest.builder()
                .bucket(s3Properties.s3BucketName())
                .delete(Delete.builder().objects(list).build())
                .build();
    }

    private void handlePartialDeleteErrors(DeleteObjectsResponse response) {
        List<S3Error> errors = response.errors();
        if(!errors.isEmpty()) {
            throw new ImageStorageException("이미지 일부 삭제에 실패했습니다.", errors.stream().map(S3Error::key).toList());
        }
    }
}
