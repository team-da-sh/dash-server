package be.dash.dashserver.api.shceduler;

import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import be.dash.dashserver.core.exception.ImageStorageException;
import be.dash.dashserver.core.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Profile("dev")
@Component
@Slf4j
@RequiredArgsConstructor
public class S3CleanUpScheduler {

    private final ImageService imageService;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanUp() {
        log.info("S3 정리 작업 시작 {}", LocalDateTime.now());
        try {
            imageService.cleanUpUnusedProfileImages();
        } catch (ImageStorageException e) {
            log.error("S3 정리 작업 실패: {}", e.getMessage());
            if (!e.getFailedKeys().isEmpty()) {
                log.error("삭제 실패한 키: {}", e.getFailedKeys());
            }
        }
        log.info("S3 정리 작업 끝 {}", LocalDateTime.now());
    }
}
