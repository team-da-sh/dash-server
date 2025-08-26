package be.dash.dashserver.api.core.member.dto;

public record StatusCount(
        String status,
        long count
) {
}
