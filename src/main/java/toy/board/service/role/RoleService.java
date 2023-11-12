package toy.board.service.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.service.member.MemberService;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RoleService {

    private final MemberService memberService;

    @Transactional
    public void promoteMemberRole(Long masterId, Long targetId) {
        Member master = memberService.findMemberWithFetchJoinProfile(masterId);
        Member target = memberService.findMemberWithFetchJoinProfile(targetId);

        master.updateRole(target);
    }
}
