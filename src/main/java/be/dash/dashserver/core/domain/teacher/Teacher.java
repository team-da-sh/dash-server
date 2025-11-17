package be.dash.dashserver.core.domain.teacher;

import java.util.List;
import be.dash.dashserver.core.domain.lesson.Images;
import be.dash.dashserver.core.domain.lesson.Videos;
import be.dash.dashserver.core.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Teacher {

    private static final Member UNKNOWN_MEMBER = Member.builder()
            .isDeleted(true)
            .build();

    private final Long id;
    private final Member member;
    private final String nickname;
    private final String detail;
    private final List<String> educations;
    private final List<String> experiences;
    private final List<String> prizes;
    private final String instagram;
    private final String youtube;
    private final Images images;
    private final Videos videos;
    private final long lessonCount;

    @Builder
    public Teacher(
            Long id,
            Member member,
            String nickname,
            String detail,
            List<String> educations,
            List<String> experiences,
            List<String> prizes,
            String instagram,
            String youtube,
            List<String> imageUrls,
            List<String> videoUrls,
            long lessonCount) {
        this.id = id;
        this.member = member != null ? member : UNKNOWN_MEMBER;
        this.nickname = nickname;
        this.detail = detail;
        this.educations = educations;
        this.experiences = experiences;
        this.prizes = prizes;
        this.instagram = instagram;
        this.youtube = youtube;
        this.images = new Images(imageUrls);
        this.videos = new Videos(videoUrls);
        this.lessonCount = lessonCount;
    }

    public String getNickname() {
        if (member == null || member.isDeleted()) {
            return "알 수 없음";
        } else {
            return nickname;
        }
    }

    public String getInstagram() {
        if (member == null || member.isDeleted()) {
            return "알 수 없음";
        } else {
            return instagram;
        }
    }
    public String getYoutube() {
        if (member == null || member.isDeleted()) {
            return "알 수 없음";
        } else {
            return youtube;
        }
    }

    public String getDetail() {
        if (member == null || member.isDeleted()) {
            return "탈퇴한 회원입니다.";
        } else {
            return detail;
        }
    }

    public List<String> getEducations() {
        if (member == null || member.isDeleted()) {
            return List.of();
        } else {
            return educations;
        }
    }
    public List<String> getExperiences() {
        if (member == null || member.isDeleted()) {
            return List.of();
        } else {
            return experiences;
        }
    }

    public List<String> getPrizes() {
        if (member == null || member.isDeleted()) {
            return List.of();
        } else {
            return prizes;
        }
    }

    public String getRepresentativeImageUrl() {
        if (member == null || member.isDeleted()) {
            return null;
        }
        return images.getFirstImage();
    }

    public List<String> getImageUrls() {
        if (member == null || member.isDeleted()) {
            return List.of();
        } else {
            return images.getImageUrls();
        }
    }

    public List<String> getVideoUrls() {
        if (member == null || member.isDeleted()) {
            return List.of();
        } else {
            return videos.getVideoUrls();
        }
    }

}
