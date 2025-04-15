package be.dash.dashserver.core.domain.member.command;

import be.dash.dashserver.core.domain.member.Member;

public record OnboardCommand(
        long id,
        String name,
        String phoneNumber,
        String nickname,
        String profileImageUrl
) {
    public Member toMember() {
        return Member.builder()
                .id(id)
                .name(name)
                .phoneNumber(phoneNumber)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
