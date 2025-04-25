package be.dash.dashserver.core.image;

import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.exception.BadGatewayException;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageManager imageManager;
    private final MemberRepository memberRepository;

    public String upload(MultipartFile file) {
        try {
            return imageManager.uploadImage(file);
        } catch (IOException e) {
            throw new BadGatewayException("이미지 업로드 중에 오류가 발생했습니다.");
        }
    }

    public void cleanUpUnusedProfileImages() {
        List<String> profileImages = memberRepository.findAllProfileImages();
        List<String> keysToDelete = imageManager.getAllKeys().stream()
                .filter(key -> !profileImages.contains(key)).toList();
        imageManager.deleteAllByKeys(keysToDelete);
    }
}
