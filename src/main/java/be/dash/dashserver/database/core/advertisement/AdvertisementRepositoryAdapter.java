package be.dash.dashserver.database.core.advertisement;

import java.util.List;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.advertisement.Advertisement;
import be.dash.dashserver.core.domain.advertisement.service.AdvertisementRepository;
import be.dash.dashserver.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdvertisementRepositoryAdapter implements AdvertisementRepository {

    private final AdvertisementJpaRepository advertisementJpaRepository;

    @Override
    public List<Advertisement> getAdvertisement() {
        return advertisementJpaRepository.findAll().stream().map(AdvertisementJpaEntity::toDomain).toList();
    }

    @Override
    public Advertisement findById(Long id) {
        return advertisementJpaRepository.findById(id)
                .map(AdvertisementJpaEntity::toDomain)
                .orElseThrow(() -> new NotFoundException("광고를 찾을 수 없습니다."));
    }

    @Override
    public Advertisement save(Advertisement advertisement) {
        return advertisementJpaRepository.save(new AdvertisementJpaEntity(advertisement)).toDomain();
    }
}
