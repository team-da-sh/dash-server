package be.dash.dashserver.core.domain.member.command;

import be.dash.dashserver.core.domain.member.Member;

public record MemberUpdateCommand(long memberId,
                                  String name,
                                  String phoneNumber,
                                  String profileImageUrl) {
    public Member toMember() {
        return Member.builder()
                .id(memberId)
                .name(name)
                .phoneNumber(phoneNumber)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
