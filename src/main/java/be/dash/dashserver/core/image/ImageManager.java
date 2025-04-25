package be.dash.dashserver.core.image;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageManager {
    String uploadImage(MultipartFile file) throws IOException;
    List<String> getAllKeys();
    void deleteAllByKeys(List<String> keysToDelete);
}
