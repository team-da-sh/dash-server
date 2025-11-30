package be.dash.dashserver.api.core.advertisement.dto;

import be.dash.dashserver.core.domain.advertisement.Advertisement;

public record AdvertisementResponse(Long id, String imageUrl, String description) {

    public AdvertisementResponse(Advertisement advertisement) {
        this(advertisement.id(), advertisement.imageUrl(), advertisement.description());
    }
}
