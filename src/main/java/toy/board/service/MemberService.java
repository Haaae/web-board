package toy.board.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final MailService mailService;
    private final RedisService redisService;

    @Value("${spring.mail.properties.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;
    private final String REDIS_PREFIX = "AuthCode";
    private final String EMAIL_TITLE = "My Poker Hand History 이메일 인증 번호";

    /**
     * member를 찾는 과정에서 member가 없거나 비밀번호가 다르는 등 예외상황에서 항상 exception이 발생하므로 return된 member는 항상 not null이다.
     * @param username
     * @param password
     * @return NotNull
     */
    public Member login(final String username, final String password) {
        Optional<Member> findMember = memberRepository.findMemberByUsername(username);
        return findMember
                .map(member -> validateLoginTypeAndPassword(password, member))
                .orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND));
    }

    /*
    DB 중복 검사에 관한 글
    : https://www.inflearn.com/questions/59250/%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94-unique-index-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
     */
    @Transactional
    public Member join(final String username, final String password, final String nickname) {

        // username 중복 검사
        validateDuplicateUsername(username);
        // nickname 중복 검사
        validateDuplicateNickname(nickname);

        // Create entity
        Profile profile = Profile.builder(nickname).build();
        Login login = new Login(passwordEncoder.encode(password));
        Member member = Member.builder(username, login, profile, LoginType.LOCAL_LOGIN, UserRole.USER).build();
        member.changeLogin(login);

        // save. cascade로 인해 member만 저장해도 profile과 login이 저장된다.
        // TODO: 동시성 문제는 DataIntegrityViolationException을 ControllerAdvice에서 공통 처리한다.
        memberRepository.save(member);
        return member;
    }

    public void sendCodeToEmail(final String email) {
        validateDuplicateUsername(email);
        String authCode = createAuthCode();
        mailService.sendMail(email, EMAIL_TITLE, authCode);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(REDIS_PREFIX + email, authCode, authCodeExpirationMillis);
    }

    @Transactional
    public boolean verifiedCode(final String email, final String authCode) {
        validateDuplicateUsername(email);
        return redisService.deleteIfValueExistAndEqualTo(REDIS_PREFIX + email, authCode);
    }

    @Transactional
    public void withdrawal(final Long loginMemberId, final String password) {
        Optional<Member> findMember = memberRepository.findMemberById(loginMemberId);

        if (findMember.isEmpty()) {
            throw new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND);
        }

        findMember.ifPresent(member -> validatePassword(password, member));

        memberRepository.deleteById(loginMemberId);
    }

    private String createAuthCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateDuplicateUsername(final String username) {
        if (memberRepository.existsByUsername(username)) {
            log.debug("=== {} exception occur username: {} ===", this.getClass().getName(), username);
            throw new BusinessException(ExceptionCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateNickname(final String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            log.debug("=== {} exception occur username: {} ===", this.getClass().getName(), nickname);
            throw new BusinessException(ExceptionCode.DUPLICATE_NICKNAME);
        }
    }

    private Member validateLoginTypeAndPassword(final String password, final Member member) {
        if (isLoginTypeNotMatch(member)) {
            throw new BusinessException(ExceptionCode.NOT_MATCH_LOGIN_TYPE);
        }

        validatePassword(password, member);

        return member;
    }

    private void validatePassword(final String password, final Member member) {
        if (isPasswordNotMatch(password, member)) {
            throw new BusinessException(ExceptionCode.NOT_MATCH_PASSWORD);
        }
    }

    private boolean isPasswordNotMatch(final String password, final Member member) {
        return !passwordEncoder.matches(password, member.getLogin().getPassword());
    }

    private boolean isLoginTypeNotMatch(final Member member) {
        return member.getLoginType() != LoginType.LOCAL_LOGIN;
    }
}
