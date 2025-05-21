package be.dash.dashserver.database.core.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.database.fixture.MemberJpaEntityFixture;

@DataJpaTest
@Import({
        MemberRepositoryAdapter.class,
        ObjectMapper.class
})
class MemberRepositoryAdapterTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("온보딩시에 멤버, 학생, 장르 엔티티가 저장된다.")
    void onboard() {
        // given
        MemberJpaEntity member = MemberJpaEntityFixture.createWithoutOnboarding();
        memberJpaRepository.save(member);
        Member domainMember = Member.builder()
                .id(1L)
                .name("실명")
                .phoneNumber("01000000000")
                .nickname("닉네임")
                .profileImageUrl("image.com")
                .build();
        // when
        memberRepository.onboard(domainMember);

        // then
        Assertions.assertThat(memberJpaRepository.findById(1L).get().getName()).isEqualTo("실명");
    }
}
