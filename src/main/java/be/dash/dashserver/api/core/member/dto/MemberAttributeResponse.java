package be.dash.dashserver.api.core.member.dto;

import be.dash.dashserver.core.domain.member.Role;

public record MemberAttributeResponse(
        long userId,
        Role role,
        Long teachId
) {
        public MemberAttributeResponse (long userId, Role role, Long teachId) {
                this.userId = userId;
                this.role = role;
                this.teachId = teachId;
        }
}
