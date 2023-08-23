package toy.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import toy.board.dto.login.response.EmailVerificationResponse;
import toy.board.dto.login.request.EmailVerificationRequest;
import toy.board.dto.login.request.JoinRequest;
import toy.board.dto.login.request.LoginRequest;
import toy.board.dto.login.request.SendEmailVerificationRequest;
import toy.board.dto.login.request.WithdrawalRequest;
import toy.board.dto.login.response.JoinResponse;
import toy.board.dto.login.response.LoginResponse;
import toy.board.dto.user.FindUserResponse;
import toy.board.entity.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.member.MemberRepository;
import toy.board.service.MemberService;
import toy.board.constant.SessionConst;

import java.util.Objects;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)  // @Autowired는 생성자가 한 개일 때 생략할 수 있음
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // Http Message Body를 객체에 매핑하는 @RequestBody는 객체의 프로퍼티가 하나라도 맞지 않으면 에러가 발생
    // Error Type: MethodArgumentNotValidException.class
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            // TODO: 2023-08-02 DTO인 loginRequest의 어노테이션 유효성 검증 로직 구현
            HttpServletRequest request
    ) {
        // valid 실패 시 자동으로 에러 발생하므로 바로 member를 찾는다.
        Member loginMember = memberService.login(loginRequest.username(),
                loginRequest.password());

        // 찾은 member를 세션에 넣어준다. 세션이 있으면 반환, 없으면 생성.
        // JESSIONID는 톰캣이 자동으로 발급
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember.getId());

        return ResponseEntity.ok(LoginResponse.of(loginMember));
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {

            Objects.requireNonNull(request.getSession(false)).invalidate();
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity withdrawal(
            @RequestBody @Valid WithdrawalRequest withdrawalRequest,
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        // 세션에서 사용자 정보 가져오기. 세션이 null이면 커스텀 예외 throws
            Long loginMemberId = (Long) Objects.requireNonNull(session)
                    .getAttribute(SessionConst.LOGIN_MEMBER);

            memberService.withdrawal(loginMemberId, withdrawalRequest.password());

            return new ResponseEntity<>(HttpStatus.OK);
    }

    /*  회원가입시 필요한 정보

        필수 정보
            - username(Login.class)
            - Password(Login.class)
            - Nickname(Profile.class)

        선택 입력 정보
            - 본인인증 정보

     */
    @PostMapping
    public ResponseEntity<JoinResponse> join(@RequestBody @Valid JoinRequest joinRequest) {
        Member member = memberService.join(joinRequest.username(), joinRequest.password(),
                joinRequest.nickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(JoinResponse.of(member));
    }

    @GetMapping("/usernames/{username}")
    public ResponseEntity<FindUserResponse> findUserByUsername(@PathVariable String username) {
        Optional<Member> member = memberRepository.findMemberByUsername(username);

        return ResponseEntity.ok(
                FindUserResponse.of(
                        member.orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND)))
        );
    }

    @GetMapping("/nicknames/{nickname}")
    public ResponseEntity<FindUserResponse> findUserByNickname(@PathVariable String nickname) {
        Optional<Member> member = memberRepository.findMemberByNickname(nickname);

        return ResponseEntity.ok(
                FindUserResponse.of(
                        member.orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND)))
        );
    }

    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestBody @Valid SendEmailVerificationRequest request) {
        memberService.sendCodeToEmail(request.email());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/emails/verifications")
    public ResponseEntity<EmailVerificationResponse> verificationEmail(@RequestBody @Valid EmailVerificationRequest request) {
        boolean result = memberService.verifiedCode(request.email(), request.authCode());

        return ResponseEntity.ok(EmailVerificationResponse.of(result));
    }
}
