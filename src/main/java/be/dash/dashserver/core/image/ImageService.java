package be.dash.dashserver.core.image;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.exception.BadRequestException;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
public class ImageService {

    private static final List<String> IMAGE_EXTENSIONS = List.of(
            "image/jpeg", "image/png", "image/jpg", "image/webp", "image/heic", "image/heif"
    );

    private final ImageUploader imageUploader;
    private final ImageReader imageReader;
    private final ImageDeleter imageDeleter;
    private final MemberRepository memberRepository;

    public String upload(MultipartFile file) {
        validateExtension(file);
        return imageUploader.upload(file);
    }

    public void cleanUpUnusedProfileImages() {
        List<String> profileImages = memberRepository.findAllProfileImages();
        List<String> keysToDelete = imageReader.getAllKeys().stream()
                // images 도메인 용도별로 분리 후 도메인 로직으로 넣기
                .filter(key -> !profileImages.contains(key)).toList();
        imageDeleter.deleteAllByKeys(keysToDelete);
    }

    private void validateExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if (!IMAGE_EXTENSIONS.contains(contentType)) {
            throw new BadRequestException("이미지 확장자는 jpg, png, webp, heic, heif만 가능합니다.");
        }
    }
}
