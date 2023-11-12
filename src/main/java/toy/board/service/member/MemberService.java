package toy.board.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.auth.Login;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;
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

    /**
     * @param username
     * @param password
     * @return member를 찾는 과정에서 member가 없거나 비밀번호가 다르는 등 예외상황에서 항상 exception이 발생하므로 return된 member는 항상 not null이다.
     */
    public Member login(final String username, final String password) {
        Member findMember = findMemberByUsernameWithFetchJoinLogin(username);

        findMember.validateLoginType(LoginType.LOCAL_LOGIN);
        checkPassword(password, findMember.getPassword());
        return findMember;
    }

    /*
    DB 중복 검사에 관한 글
    : https://www.inflearn.com/questions/59250/%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94-unique-index-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
     */
    @Transactional
    public Member join(final String username, final String password, final String nickname) {
        checkUsernameDuplication(username);
        checkNicknameDuplication(nickname);

        Profile profile = Profile.builder(nickname).build();
        Login login = new Login(passwordEncoder.encode(password));
        Member member = Member.builder(
                        username,
                        login,
                        profile,
                        LoginType.LOCAL_LOGIN,
                        UserRole.USER
                )
                .build();
        member.changeLogin(login);

        // TODO: 동시성 문제는 DataIntegrityViolationException을 ControllerAdvice에서 공통 처리한다.
        // save. cascade로 인해 member만 저장해도 profile과 login이 저장된다.
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public void withdrawal(final Long loginMemberId) {
        Member findMember = findMemberWithFetchJoinProfile(loginMemberId);

        findMember.changeAllPostAndCommentWriterToNull();

        memberRepository.deleteById(loginMemberId);
    }

    public void checkUsernameDuplication(final String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_DUPLICATE);
        }
    }

    private void checkNicknameDuplication(final String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_DUPLICATE);
        }
    }

    private void checkPassword(final String enteredPassword, final String password) {
        if (!passwordEncoder.matches(enteredPassword, password)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_AUTHENTICATION);
        }
    }

    public Member findMemberWithFetchJoinProfile(Long memberId) {
        return memberRepository.findMemberWithFetchJoinProfile(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));
    }

    private Member findMemberByUsernameWithFetchJoinLogin(String username) {
        return memberRepository.findMemberByUsernameWithFetchJoinLogin(username)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));
    }
}
