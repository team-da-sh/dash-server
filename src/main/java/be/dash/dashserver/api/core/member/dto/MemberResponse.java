package be.dash.dashserver.api.core.member.dto;

import be.dash.dashserver.core.domain.member.Member;

public record MemberResponse(String profileImageUrl,
                             String name,
                             String phoneNumber) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getProfileImageUrl(),
                member.getName(),
                member.getPhoneNumber());
    }
}
