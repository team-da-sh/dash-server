package be.dash.dashserver.core.domain.member.service;

import java.util.List;
import java.util.Optional;
import be.dash.dashserver.core.domain.member.AuthMember;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.SocialProvider;

public interface MemberRepository {
    AuthMember findBySocialIdAndProviderOrNull(String socialId, SocialProvider provider);

    AuthMember save(AuthMember authMember);

    Member save(Member member);

    Member findById(long id);

    void onboard(Member member);

    void updateRole(Long id, Role role);

    List<Member> findAllByMemberIds(List<Long> memberIds);

    void update(Member member);

    Optional<String> findNicknameById(long memberId);

    List<String> findAllProfileImages();
}
