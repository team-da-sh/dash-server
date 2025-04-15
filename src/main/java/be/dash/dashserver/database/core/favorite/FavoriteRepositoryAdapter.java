package be.dash.dashserver.database.core.favorite;

import org.springframework.stereotype.Component;
import be.dash.dashserver.core.domain.favorite.service.FavoriteRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FavoriteRepositoryAdapter implements FavoriteRepository {

    private final FavoriteJpaRepository favoriteJpaRepository;

    @Override
    public int getFavoriteCountByMemberId(Long memberId) {
        return favoriteJpaRepository.countByMemberId(memberId);
    }
}
