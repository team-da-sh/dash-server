package be.dash.dashserver.core.domain.teacher.service;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import be.dash.dashserver.ServiceSliceTest;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Keyword;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.core.domain.teacher.TeacherLessonGenres;
import be.dash.dashserver.core.domain.teacher.command.CreateTeacherCommand;
import be.dash.dashserver.core.domain.teacher.command.TeacherUpdateCommand;
import be.dash.dashserver.core.domain.teacher.service.dto.MyTeacherProfileDetailResult;
import be.dash.dashserver.core.domain.teacher.service.dto.MyTeacherProfileResult;
import be.dash.dashserver.core.exception.ConflictException;
import be.dash.dashserver.core.exception.NotFoundException;
import be.dash.dashserver.core.fixture.LessonFixture;
import be.dash.dashserver.core.fixture.MemberFixture;
import be.dash.dashserver.core.fixture.TeacherFixture;
import be.dash.dashserver.database.core.member.MemberJpaEntity;
import be.dash.dashserver.database.core.member.MemberJpaRepository;
import be.dash.dashserver.database.core.teacher.TeacherImageJpaEntity;
import be.dash.dashserver.database.core.teacher.TeacherImageJpaRepository;
import be.dash.dashserver.database.core.teacher.TeacherJpaEntity;
import be.dash.dashserver.database.core.teacher.TeacherJpaRepository;
import be.dash.dashserver.database.core.teacher.TeacherVideoJpaEntity;
import be.dash.dashserver.database.core.teacher.TeacherVideoJpaRepository;
import be.dash.dashserver.database.fixture.MemberJpaEntityFixture;
import be.dash.dashserver.database.fixture.TeacherJpaEntityFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TeacherServiceTest extends ServiceSliceTest {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeacherImageRepository teacherImageRepository;
    @Autowired
    private TeacherJpaRepository teacherJpaRepository;
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private TeacherImageJpaRepository teacherImageJpaRepository;
    @Autowired
    private TeacherVideoJpaRepository teacherVideoJpaRepository;

    @DisplayName("기본 정렬 조건에 맞게 댄서들을 정렬 후 조회한다.")
    @Test
    void search() {
        createLessons();

        List<TeacherLessonGenres> searched = teacherService.search(new Keyword());

        assertAll(
                () -> assertThat(searched.get(0).teacher().getId()).isEqualTo(1),
                () -> assertThat(searched.get(0).genres()).containsExactly(Genre.FEMALE_HIPHOP, Genre.HIPHOP),
                () -> assertThat(searched.get(1).teacher().getId()).isEqualTo(2),
                () -> assertThat(searched.get(1).genres()).containsExactly(Genre.HIPHOP),
                () -> assertThat(searched.get(2).teacher().getId()).isEqualTo(3),
                () -> assertThat(searched.get(2).genres()).containsExactly(Genre.FEMALE_HIPHOP)
        );
    }

    @DisplayName("유저의 선생님 프로필을 조회한다")
    @Test
    void findMyTeacherProfile() {
        // given
        registerTeacher();

        // when
        MyTeacherProfileResult myTeacherProfileResult = teacherService.findMyTeacherProfile(1);

        // then
        assertAll(
                () -> Assertions.assertThat(myTeacherProfileResult.profileImage()).isEqualTo("www.example.com/teacher12.png"),
                () -> Assertions.assertThat(myTeacherProfileResult.nickname()).isEqualTo("nickname"),
                () -> Assertions.assertThat(myTeacherProfileResult.instagram()).isEqualTo("@hong_dancer"),
                () -> Assertions.assertThat(myTeacherProfileResult.youtube()).isEqualTo("youtube.com/hong_dancer")
        );
    }

    @DisplayName("유저의 선생님 상세 프로필을 조회한다")
    @Test
    void findMyTeacherDetailProfile() {
        // given
        registerTeacher();

        // when
        MyTeacherProfileDetailResult myTeacherProfileDetail = teacherService.findMyTeacherProfileDetail(1);

        // then
        assertAll(
                () -> Assertions.assertThat(myTeacherProfileDetail.profileImage()).isEqualTo("www.example.com/teacher12.png"),
                () -> Assertions.assertThat(myTeacherProfileDetail.instagram()).isEqualTo("@hong_dancer"),
                () -> Assertions.assertThat(myTeacherProfileDetail.youtube()).isEqualTo("youtube.com/hong_dancer"),
                () -> Assertions.assertThat(myTeacherProfileDetail.detail()).isEqualTo("경력 10년의 힙합 댄서"),
                () -> Assertions.assertThat(myTeacherProfileDetail.videos()).containsExactly("www.example.com/teacher12_video.mp4"),
                () -> Assertions.assertThat(myTeacherProfileDetail.educations()).containsExactly("한국예술대학교 댄스학과"),
                () -> Assertions.assertThat(myTeacherProfileDetail.experiences()).containsExactly("다양한 공연 및 강의 경험"),
                () -> Assertions.assertThat(myTeacherProfileDetail.prizes()).containsExactly("앱잼1등")
        );
    }

    @DisplayName("선생님 프로필을 수정한다")
    @Test
    void updateTeacherProfile() {
        // given
        registerTeacher();

        // when
        teacherService.updateTeacherProfile(new TeacherUpdateCommand(
                1L,
                "updated_detail",
                List.of("www.example.com/updated.png"),
                "@updated_instagram",
                "updated_youtube.com",
                List.of("updated_education"),
                List.of("updated_experience"),
                List.of("updated_prize"),
                List.of("www.example.com/updated_video.mp4",
                        "www.example.com/updated_video2.mp4")
        ));

        TeacherJpaEntity updatedTeacher = teacherJpaRepository.findById(1L).get();
        // then
        assertAll(
                ()-> Assertions.assertThat(teacherImageJpaRepository.findAllByTeacherId(1L)).hasSize(1),
                ()-> Assertions.assertThat(teacherVideoJpaRepository.findAllByTeacherId(1L)).hasSize(2),
                ()-> Assertions.assertThat(updatedTeacher.getDetail()).isEqualTo("updated_detail"),
                ()-> Assertions.assertThat(updatedTeacher.getInstagram()).isEqualTo("@updated_instagram"),
                ()-> Assertions.assertThat(updatedTeacher.getYoutube()).isEqualTo("updated_youtube.com"),
                ()-> Assertions.assertThat(updatedTeacher.getEducation()).isEqualTo("updated_education"),
                ()-> Assertions.assertThat(updatedTeacher.getExperience()).isEqualTo("updated_experience"),
                ()-> Assertions.assertThat(updatedTeacher.getPrize()).isEqualTo("updated_prize")
        );
    }

    @DisplayName("선생님 프로필을 수정시 instagram이 중복되면 예외가 발생한다")
    @Test
    void failUpdateTeacherProfileOnDuplicatedInstagram() {
        // given
        registerTeacher();
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("testnickname", 2));

        // when then
        Assertions.assertThatThrownBy(() -> teacherService.updateTeacherProfile(new TeacherUpdateCommand(
                1L,
                "updated_detail",
                List.of("www.example.com/updated.png"),
                "@hong_dancer",
                "updated_youtube.com",
                List.of("updated_education"),
                List.of("updated_experience"),
                List.of("updated_prize"),
                List.of("www.example.com/updated_video.mp4",
                        "www.example.com/updated_video2.mp4")
        ))).isInstanceOf(ConflictException.class);
    }

    @DisplayName("선생님 프로필을 수정시 잘못된 memberId를 입력하면 예외가 발생한다")
    @Test
    void failUpdateTeacherProfileOnWrongId() {
        // given
        registerTeacher();

        // when then
        Assertions.assertThatThrownBy(() -> teacherService.updateTeacherProfile(new TeacherUpdateCommand(
                2L,
                "updated_detail",
                List.of("www.example.com/updated.png"),
                "@updated_instagram",
                "updated_youtube.com",
                List.of("updated_education"),
                List.of("updated_experience"),
                List.of("updated_prize"),
                List.of("www.example.com/updated_video.mp4",
                        "www.example.com/updated_video2.mp4")
        ))).isInstanceOf(NotFoundException.class)
                .hasMessage("선생님 프로필이 존재하지 않습니다.");
    }

    @DisplayName("선생님 프로필을 등록한다.")
    @Test
    void create() {
        // given
        memberJpaRepository.save(MemberJpaEntityFixture.create());

        // when
        teacherService.create(new CreateTeacherCommand(
                1L,
                "instagram",
                "youtube",
                List.of("한국예술대학교 댄스학과"),
                List.of("다양한 공연 및 강의 경험"),
                List.of("앱잼1등"),
                "detaildetaildetaildetaildetail",
                List.of("www.example.com/image.mp4"),
                List.of("www.example.com/video.mp4")
        ));

        // then
        TeacherJpaEntity teacher = teacherJpaRepository.findById(1L).get();
        assertAll(
                () -> assertThat(teacher.getDetail()).isEqualTo("detaildetaildetaildetaildetail"),
                () -> assertThat(teacher.getInstagram()).isEqualTo("instagram"),
                () -> assertThat(teacher.getYoutube()).isEqualTo("youtube"),
                () -> assertThat(teacher.getEducation()).isEqualTo("한국예술대학교 댄스학과"),
                () -> assertThat(teacher.getExperience()).isEqualTo("다양한 공연 및 강의 경험"),
                () -> assertThat(teacher.getPrize()).isEqualTo("앱잼1등"),
                () -> assertThat(teacherImageJpaRepository.findAllByTeacherId(1L)).hasSize(1),
                () -> assertThat(teacherVideoJpaRepository.findAllByTeacherId(1L)).hasSize(1)
        );
    }

    @DisplayName("선생님 프로필을 등록시 instagram이 중복되면 예외를 발생한다..")
    @Test
    void failCreateOnDuplicatedInstagram() {
        // given
        registerTeacher();
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("testnickname", 2));

        // when, then
        Assertions.assertThatThrownBy(() ->teacherService.create(new CreateTeacherCommand(
                1L,
                "@hong_dancer",
                "youtube",
                List.of("한국예술대학교 댄스학과"),
                List.of("다양한 공연 및 강의 경험"),
                List.of("앱잼1등"),
                "detaildetaildetaildetaildetail",
                List.of("www.example.com/image.mp4"),
                List.of("www.example.com/video.mp4"))
        )).isInstanceOf(ConflictException.class);
    }

    @DisplayName("선생님 프로필을 등록시 youtube가 중복되면 예외를 발생한다..")
    @Test
    void failCreateOnDuplicatedYoutube() {
        // given
        registerTeacher();
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("testnickname", 2));

        // when, then
        Assertions.assertThatThrownBy(() ->teacherService.create(new CreateTeacherCommand(
                1L,
                "@hong_dancer2",
                "youtube.com/hong_dancer",
                List.of("한국예술대학교 댄스학과"),
                List.of("다양한 공연 및 강의 경험"),
                List.of("앱잼1등"),
                "detaildetaildetaildetaildetail",
                List.of("www.example.com/image.mp4"),
                List.of("www.example.com/video.mp4"))
        )).isInstanceOf(ConflictException.class);
    }

    private void registerTeacher() {
        // given
        MemberJpaEntity member = MemberJpaEntityFixture.createWithNickname("nickname", 1);
        memberJpaRepository.save(member);
        TeacherJpaEntity teacher = TeacherJpaEntityFixture.create(member);
        teacherJpaRepository.save(teacher);
        TeacherImageJpaEntity teacherImage = TeacherImageJpaEntity.builder()
                .teacherId(teacher.getId())
                .imageUrl("www.example.com/teacher12.png")
                .build();
        teacherImageJpaRepository.save(teacherImage);
        TeacherVideoJpaEntity teacherVideo = TeacherVideoJpaEntity.builder()
                .teacherId(teacher.getId())
                .videoUrl("www.example.com/teacher12_video.mp4")
                .build();
        teacherVideoJpaRepository.save(teacherVideo);
    }

    private void createLessons() {
        createTeacher(1);
        createTeacher(2);
        createTeacher(3);

        lessonRepository.save(LessonFixture.create(1, 1, Genre.HIPHOP, Level.BEGINNER));
        lessonRepository.save(LessonFixture.create(1, 1, Genre.FEMALE_HIPHOP, Level.BEGINNER));
        lessonRepository.save(LessonFixture.create(1, 1, Genre.FEMALE_HIPHOP, Level.BEGINNER));
        lessonRepository.save(LessonFixture.create(1, 1, Genre.FEMALE_HIPHOP, Level.BEGINNER));
        lessonRepository.save(LessonFixture.create(2, 2, Genre.HIPHOP, Level.BEGINNER));
        lessonRepository.save(LessonFixture.create(2, 2, Genre.HIPHOP, Level.BEGINNER));
        lessonRepository.save(LessonFixture.create(2, 2, Genre.HIPHOP, Level.BEGINNER));
        lessonRepository.save(LessonFixture.create(3, 3, Genre.FEMALE_HIPHOP, Level.BEGINNER));
    }

    private void createTeacher(long id) {
        Member memberWithoutId = MemberFixture.createTeacherWithNickname(String.valueOf(id), (int) id);
        memberRepository.save(memberWithoutId);
        Teacher teacherWithoutId = TeacherFixture.createWithoutId(id);
        teacherRepository.save(teacherWithoutId);
        Teacher teacher = TeacherFixture.create(id, id);
        teacherImageRepository.saveAll(teacher);
    }
}
