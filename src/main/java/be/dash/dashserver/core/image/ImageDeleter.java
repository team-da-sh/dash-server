package be.dash.dashserver.core.image;

import java.util.List;

public interface ImageDeleter {
    void deleteAllByKeys(List<String> keysToDelete);
}
