package be.dash.dashserver.core.domain.member.command;

import be.dash.dashserver.core.domain.member.Member;

public record OnboardCommand(
        long memberId,
        String name,
        String phoneNumber,
        String nickname,
        String profileImageUrl) {

    public Member toMember() {
        return Member.builder()
                .id(memberId)
                .name(name)
                .phoneNumber(phoneNumber)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
