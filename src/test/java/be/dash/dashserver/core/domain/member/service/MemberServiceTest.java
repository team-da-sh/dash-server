package be.dash.dashserver.core.domain.member.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import be.dash.dashserver.ServiceSliceTest;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.command.MemberUpdateCommand;
import be.dash.dashserver.core.fixture.MemberFixture;

public class MemberServiceTest extends ServiceSliceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("멤버 정보를 변경한다.")
    @Test
    void updateMemberInformation() {
        memberRepository.save(MemberFixture.createMember());

        memberService.updateMemberInformation(
                new MemberUpdateCommand(1L,
                        "name",
                        "01011111111",
                        "nickname",
                        "url")
        );
        Member member = memberRepository.findById(1L);

        Assertions.assertThat(member.getName()).isEqualTo("name");
        Assertions.assertThat(member.getPhoneNumber()).isEqualTo("01011111111");
        Assertions.assertThat(member.getNickname()).isEqualTo("nickname");
        Assertions.assertThat(member.getProfileImageUrl()).isEqualTo("url");
    }
}
