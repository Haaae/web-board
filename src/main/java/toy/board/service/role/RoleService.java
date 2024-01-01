package toy.board.service.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.user.MemberRepository;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RoleService {

    private final MemberRepository memberRepository;

    @Transactional
    public void promoteMemberRole(Long masterId, Long targetId) {
        Member master = memberRepository.findById(masterId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        Member target = memberRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        master.updateRole(target);
    }
}
