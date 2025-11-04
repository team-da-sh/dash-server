package be.dash.dashserver.core.domain.lesson;

import java.util.List;
import lombok.Getter;

@Getter
public class Images {
    private List<String> imageUrls;

    public Images(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getFirstImage() {
        if (!imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return null;
    }
}
