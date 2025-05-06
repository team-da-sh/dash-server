package be.dash.dashserver.api.core.teacher.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import be.dash.dashserver.core.domain.teacher.command.TeacherUpdateCommand;

public record TeacherUpdateRequest(
        @Size(max = 300) @NotBlank String detail,
        List<String> imageUrls,
        String instagram,
        String youtube,
        List<@Size(max = 30) String> educations,
        List<@Size(max = 30) String> experiences,
        List<@Size(max = 30) String> prizes,
        @Size(min = 1) List<String> videoUrls
) {
    public TeacherUpdateCommand toCommand(long memberId) {
        return new TeacherUpdateCommand(memberId, detail, imageUrls, instagram, youtube, educations, experiences, prizes, videoUrls);
    }

}
