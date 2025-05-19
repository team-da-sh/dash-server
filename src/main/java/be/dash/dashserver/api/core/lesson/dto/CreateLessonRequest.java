package be.dash.dashserver.api.core.lesson.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.command.CreateLessonCommand;

public record CreateLessonRequest(
        @Size(min = 1, message = "이미지 url은 최소 1개입니다.")
        @NotNull(message = "이미지 url 리스트는 null일 수 없습니다.")
        List<@NotBlank(message = "이미지 url은 공백일 수 없습니다.") String> imageUrls,
        @NotBlank(message = "클래스명은 공백일 수 없습니다.")
        @Size(max = 30, message = "클래스명은 30자 이하입니다.")
        String name,
        @NotBlank(message = "상세설명은 공백일 수 없습니다.")
        @Size(max = 300, message = "상세설명은 300자 이하입니다.")
        String detail,
        @Pattern(regexp = "^[0-9]+$", message = "예약인원은 숫자여야 합니다.")
        long maxReservationCount,
        @NotNull(message = "장르는 필수입니다.")
        Genre genre,
        @NotNull(message = "레벨은 필수입니다.")
        Level level,
        @NotBlank(message = "추천은 공백일 수 없습니다.")
        @Size(max = 200, message = "추천은 200자 이하입니다.")
        String recommendation,
        @Pattern(regexp = "^[0-9]+$", message = "가격은 숫자여야 합니다.")
        @Positive(message = "가격은 양수여야 합니다.")
        int price,
        String location,
        String streetAddress,
        String oldStreetAddress,
        String detailedAddress,
        @NotNull(message = "시간은 필수입니다.")
        @Size(min = 1, message = "시간은 최소 1개입니다.")
        List<RoundRequest> times
) {

    public CreateLessonCommand toCommand(Long memberId) {
        return new CreateLessonCommand(
                imageUrls,
                name,
                detail,
                maxReservationCount,
                genre,
                level,
                recommendation,
                price,
                location,
                streetAddress,
                oldStreetAddress,
                detailedAddress,
                times,
                memberId
        );
    }
}
