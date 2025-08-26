package be.dash.dashserver.database.core.member;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import be.dash.dashserver.core.domain.member.AuthMember;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.SocialProvider;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public AuthMember findBySocialIdAndProviderOrNull(String socialId, SocialProvider provider) {
        return memberJpaRepository.findBySocialIdAndProvider(socialId, provider)
                .map(MemberJpaEntity::toAuthMember)
                .orElseGet(() -> null);
    }

    @Override
    public AuthMember save(AuthMember authMember) {
        return memberJpaRepository.save(MemberJpaEntity.fromDomain(authMember)).toAuthMember();
    }

    @Override
    public Member save(Member member) {
        MemberJpaEntity save = memberJpaRepository.save(new MemberJpaEntity(member));
        return Member.builder()
                .id(save.getId())
                .provider(member.getProvider())
                .socialId(member.getSocialId())
                .socialName(member.getSocialName())
                .role(member.getRole())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    @Override
    public Member findById(long id) {
        return memberJpaRepository.findById(id).map(MemberJpaEntity::toDomain)
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));
    }

    @Override
    public void onboard(Member member) {
        MemberJpaEntity memberJpaEntity = memberJpaRepository.findById(member.getId())
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));
        memberJpaEntity.updateOnboardDetails(member);
    }

    @Override
    public void updateRole(Long id, Role role) {
        memberJpaRepository.updateRole(id, role);
    }

    @Override
    public List<Member> findAllByMemberIds(List<Long> memberIds) {
        return memberJpaRepository.findAllById(memberIds).stream().map(MemberJpaEntity::toDomain).toList();
    }

    @Override
    public void update(Member member) {
        memberJpaRepository.findById(member.getId())
                .ifPresentOrElse(
                        memberJpaEntity -> {
                            memberJpaEntity.update(member);
                        },
                        () -> {
                            throw new NotFoundException("멤버를 찾을 수 없습니다.");
                        }
                );
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return memberJpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long memberId) {
        return memberJpaRepository.existsByPhoneNumberAndIdNot(phoneNumber, memberId);
    }
}
