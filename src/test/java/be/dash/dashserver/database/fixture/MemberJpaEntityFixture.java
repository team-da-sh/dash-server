package be.dash.dashserver.database.fixture;

import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.SocialProvider;
import be.dash.dashserver.database.core.member.MemberJpaEntity;

public class MemberJpaEntityFixture {
    private MemberJpaEntityFixture() {
    }

    public static MemberJpaEntity create() {
        return MemberJpaEntity.builder()
                .provider(SocialProvider.KAKAO)
                .socialId("socialId_67890")
                .socialName("facebook_user")
                .role(Role.TEACHER)
                .email("admin@example.com")
                .name("김영희")
                .phoneNumber("01087654321")
                .build();
    }

    public static MemberJpaEntity createWithNickname(String nickname, int index) {
        return MemberJpaEntity.builder()
                .provider(SocialProvider.KAKAO)
                .socialId("socialId_67890")
                .socialName("facebook_user")
                .role(Role.TEACHER)
                .email("admin@example.com")
                .name("김영희")
                .phoneNumber("0108765432" + index)
                .build();
    }

    public static MemberJpaEntity createWithoutOnboarding() {
        return MemberJpaEntity.builder()
                .provider(SocialProvider.KAKAO)
                .socialId("socialId")
                .socialName("socialName")
                .role(Role.TEACHER)
                .email("email")
                .build();

    }
}
