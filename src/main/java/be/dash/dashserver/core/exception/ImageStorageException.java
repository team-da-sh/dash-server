package be.dash.dashserver.core.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class ImageStorageException extends RuntimeException {
    private List<String> failedKeys;
    public ImageStorageException(String message) {
        super(message);
    }

    public ImageStorageException(String message, List<String> failedKeys) {
        super(message);
        this.failedKeys = failedKeys;
    }
}
