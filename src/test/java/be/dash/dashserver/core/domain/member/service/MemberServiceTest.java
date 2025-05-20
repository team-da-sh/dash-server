package be.dash.dashserver.core.domain.member.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import be.dash.dashserver.ServiceSliceTest;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.SocialProvider;
import be.dash.dashserver.core.domain.member.command.MemberUpdateCommand;
import be.dash.dashserver.core.domain.member.command.OnboardCommand;
import be.dash.dashserver.core.exception.ConflictException;
import be.dash.dashserver.core.exception.NotFoundException;
import be.dash.dashserver.database.core.member.MemberJpaEntity;
import be.dash.dashserver.database.core.member.MemberJpaRepository;
import be.dash.dashserver.database.fixture.MemberJpaEntityFixture;

public class MemberServiceTest extends ServiceSliceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @DisplayName("멤버 정보를 변경한다.")
    @Test
    void updateMemberInformation() {
        // given
        MemberJpaEntity member = memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname1", 1));

        // when
        memberService.updateMemberInformation(
                new MemberUpdateCommand(1L,
                        "name",
                        "01011111111",
                        "nickname2",
                        "url")
        );
        MemberJpaEntity savedMember = memberJpaRepository.findById(1L).get();

        // then
        Assertions.assertThat(savedMember.getName()).isEqualTo("name");
        Assertions.assertThat(savedMember.getPhoneNumber()).isEqualTo("01011111111");
        Assertions.assertThat(savedMember.getNickname()).isEqualTo("nickname2");
        Assertions.assertThat(savedMember.getProfileImageUrl()).isEqualTo("url");
    }

    @DisplayName("멤버 정보를 변경할 때, 닉네임이 중복되면 예외가 발생한다.")
    @Test
    void failUpdateMemberInformationOnDuplicatedNickname() {
        MemberJpaEntity member1 = memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname1", 1));
        MemberJpaEntity member2 = memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname2", 2));

        Assertions.assertThatThrownBy(() -> memberService.updateMemberInformation(
                new MemberUpdateCommand(1L,
                        "name",
                        "01011111111",
                        "nickname2",
                        "url")
        )).isInstanceOf(ConflictException.class);
}

    @DisplayName("멤버 정보를 변경할 때, 전화번호가 중복되면 예외가 발생한다.")
    @Test
    void failUpdateMemberInformationOnDuplicatedPhoneNumber() {
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname1", 1));
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname2", 2));

        Assertions.assertThatThrownBy(() -> memberService.updateMemberInformation(
                new MemberUpdateCommand(1L,
                        "name",
                        "01087654322",
                        "nickname2",
                        "url")
        )).isInstanceOf(ConflictException.class);
    }

    @DisplayName("멤버 정보를 변경할 때, 멤버가 존재하지 않으면 예외가 발생한다.")
    @Test
    void failUpdateMemberInformationOnWrongMemberId() {
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname1", 1));
        Assertions.assertThatThrownBy(() -> memberService.updateMemberInformation(
                new MemberUpdateCommand(2L,
                        "name",
                        "01011111111",
                        "nickname2",
                        "url")
        )).isInstanceOf(NotFoundException.class);
    }

    @DisplayName("온보딩 시 전화번호가 중복되면 예외가 발생한다.")
    @Test
    void failOnboardOnDuplicatedPhoneNumber() {
        MemberJpaEntity onBoardMember = MemberJpaEntity
                .builder()
                .email("email1")
                .role(Role.MEMBER)
                .provider(SocialProvider.KAKAO)
                .socialId("socialId1")
                .socialName("socialName1")
                .build();
        memberJpaRepository.save(onBoardMember);
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname1", 1));

        Assertions.assertThatThrownBy(() -> memberService.onboard(
                new OnboardCommand(
                        onBoardMember.getId(),
                        "대쉬",
                        "01087654321",
                        "nickname1",
                        null)
        )).isInstanceOf(ConflictException.class);
    }

    @DisplayName("온보딩 시 닉네임이 중복되면 예외가 발생한다.")
    @Test
    void failOnboardOnDuplicatedNickname() {
        MemberJpaEntity onBoardMember = MemberJpaEntity
                .builder()
                .email("email1")
                .role(Role.MEMBER)
                .provider(SocialProvider.KAKAO)
                .socialId("socialId1")
                .socialName("socialName1")
                .build();
        memberJpaRepository.save(onBoardMember);
        memberJpaRepository.save(MemberJpaEntityFixture.createWithNickname("nickname1", 1));

        Assertions.assertThatThrownBy(() -> memberService.onboard(
                new OnboardCommand(
                        onBoardMember.getId(),
                        "대쉬",
                        "01087654321",
                        "nickname1",
                        null)
        )).isInstanceOf(ConflictException.class);
    }
}
