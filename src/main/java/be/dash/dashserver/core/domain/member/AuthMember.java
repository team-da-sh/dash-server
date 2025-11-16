package be.dash.dashserver.core.domain.member;

import lombok.Getter;

@Getter
public class AuthMember {

    private long id;
    private final SocialProvider socialProvider;
    private final String socialId;
    private final String email;
    private final String socialName;
    private final Role role;
    private boolean isDeleted;

    private AuthMember(SocialProvider socialProvider, String socialId, String email, String socialName, Role role, boolean isDeleted) {
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.email = email;
        this.socialName = socialName;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    private AuthMember(long id, SocialProvider socialProvider, String socialId, String email, String socialName, Role role, boolean isDeleted) {
        this.id = id;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.email = email;
        this.socialName = socialName;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    public static AuthMember create(SocialProvider socialProvider,
                                    String socialId,
                                    String email,
                                    String socialName) {
        return new AuthMember(socialProvider, socialId, email, socialName, Role.MEMBER, false);
    }

    public static AuthMember createWithId(long id,
                                          SocialProvider socialProvider,
                                          String socialId,
                                          String email,
                                          String socialName,
                                          Role role,
                                          boolean isDeleted) {
        return new AuthMember(id, socialProvider, socialId, email, socialName, role, isDeleted);
    }

    public void rejoin() {
        this.isDeleted = false;
    }
}
