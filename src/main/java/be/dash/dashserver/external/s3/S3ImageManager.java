package be.dash.dashserver.external.s3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import be.dash.dashserver.core.exception.BadRequestException;
import be.dash.dashserver.core.exception.ImageStorageException;
import be.dash.dashserver.core.image.ImageManager;
import be.dash.dashserver.external.config.s3.S3Config;
import be.dash.dashserver.external.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Error;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@RequiredArgsConstructor
public class S3ImageManager implements ImageManager {
    private final S3Properties s3Properties;
    private final S3Config s3Config;
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp", "image/heic", "image/heif");

    @Override
    public String uploadImage(MultipartFile image) throws IOException {
        final String key = generateImageFileName();
        final S3Client s3Client = s3Config.getS3Client();

        validateExtension(image);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.s3BucketName())
                .key(key)
                .contentType(image.getContentType())
                .contentDisposition("inline")
                .build();

        RequestBody requestBody = RequestBody.fromBytes(image.getBytes());
        s3Client.putObject(request, requestBody);
        return s3Properties.s3Endpoint() + key;
    }

    @Override
    public List<String> getAllKeys() {
        //여기도 retrey붙여야 되나? 어차피 실패하면 내일 해버리면 떙 아닌감..
        List<String> keys = new ArrayList<>();
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(s3Properties.s3BucketName())
                .build();

        ListObjectsV2Response response;
        S3Client s3Client = s3Config.getS3Client();

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

    @Override
    public void deleteAllByKeys(List<String> keysToDelete) {
        S3Client s3Client = s3Config.getS3Client();

        List<ObjectIdentifier> list = keysToDelete.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build()).toList();
        DeleteObjectsRequest deleteObjectRequest = DeleteObjectsRequest.builder()
                .bucket(s3Properties.s3BucketName())
                .delete(Delete.builder().objects(list).build())
                .build();
        DeleteObjectsResponse response;
        try {
            response = s3Client.deleteObjects(deleteObjectRequest);
        } catch (AwsServiceException | SdkClientException e) {
            throw new ImageStorageException("S3 객체 삭제에 실패했습니다.");
        }

        List<S3Error> errors = response.errors();
        if(!errors.isEmpty()) {
            throw new ImageStorageException("이미지 삭제에 실패했습니다.", errors.stream().map(S3Error::key).toList());
        }
    }

    private String generateImageFileName() {
        return UUID.randomUUID() + ".jpg";
    }

    private void validateExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if (!IMAGE_EXTENSIONS.contains(contentType)) {
            throw new BadRequestException("이미지 확장자는 jpg, png, webp, heic, heif만 가능합니다.");
        }
    }
}
