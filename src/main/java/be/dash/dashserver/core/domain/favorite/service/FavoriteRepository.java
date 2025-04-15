package be.dash.dashserver.core.domain.favorite.service;

public interface FavoriteRepository {

    int getFavoriteCountByMemberId(Long memberId);

}
