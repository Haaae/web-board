package toy.board.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.entity.user.LoginType;
import toy.board.entity.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * member를 찾는 과정에서 member가 없거나 비밀번호가 다르는 등 예외상황에서 항상 exception이 발생하므로 return된 member는 항상 not null이다.
     * @param username
     * @param password
     * @return NotNull
     */
    public Member login(String username, String password) {
        Optional<Member> findMember = memberRepository.findMemberByUsername(username);
        return findMember
                .map(member -> validateLoginTypeAndPassword(password, member))
                .orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND));
    }

    private Member validateLoginTypeAndPassword(String password, Member member) {
        if (isLoginTypeNotMatch(member)) {
            throw new BusinessException(ExceptionCode.NOT_MATCH_LOGIN_TYPE);
        }

        if (isPasswordNotMatch(password, member)) {
            throw new BusinessException(ExceptionCode.NOT_MATCH_PASSWORD);
        }

        return member;
    }
    
    private boolean isPasswordNotMatch(String password, Member member) {
        return !passwordEncoder.matches(password, member.getLogin().getPassword());
    }

    private boolean isLoginTypeNotMatch(Member member) {
        return member.getLoginType() != LoginType.LOCAL_LOGIN;
    }
}
