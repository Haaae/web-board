package toy.board.service.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.user.MemberRepository;

@Transactional(readOnly = true)
@Service
@lombok.RequiredArgsConstructor
public class MemberCheckService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public void checkUsernameDuplication(final String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_DUPLICATE);
        }
    }

    public void checkNicknameDuplication(final String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_DUPLICATE);
        }
    }

    public void checkPassword(final String enteredPassword, final String encodedPassword) {
        if (!passwordEncoder.matches(enteredPassword, encodedPassword)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_PASSWORD);
        }
    }
}
