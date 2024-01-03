package toy.board.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.domain.user.UserRole;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.user.MemberRepository;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberCheckService memberCheckService;

    /**
     * @param username
     * @param password
     * @return member를 찾는 과정에서 member가 없거나 비밀번호가 다르는 등 예외상황에서 항상 exception이 발생하므로 return된 member는 항상 not null이다.
     */
    public Member login(final String username, final String password) {
        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        memberCheckService.checkPassword(password, findMember.getPassword());
        return findMember;
    }

    /*
    DB 중복 검사에 관한 글
    : https://www.inflearn.com/questions/59250/%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94-unique-index-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
     */
    @Transactional
    public Member join(final String username, final String password, final String nickname) {
        memberCheckService.checkUsernameDuplication(username);
        memberCheckService.checkNicknameDuplication(nickname);

        Member member = Member.builder(
                        username,
                        nickname,
                        passwordEncoder.encode(password),
                        UserRole.USER
                )
                .build();

        memberRepository.save(member);
        return member;
    }

    @Transactional
    public void withdrawal(final Long loginMemberId) {
        Member findMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        findMember.changeAllPostAndCommentWriterToNull();

        memberRepository.deleteById(loginMemberId);
    }
}
