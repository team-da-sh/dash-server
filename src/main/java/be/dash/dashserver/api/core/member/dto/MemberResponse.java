package be.dash.dashserver.api.core.member.dto;

import be.dash.dashserver.core.domain.member.Member;

public record MemberResponse(String nickname,
                             String profileImageUrl,
                             String name,
                             String phoneNumber) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getName(),
                member.getPhoneNumber());
    }
}
