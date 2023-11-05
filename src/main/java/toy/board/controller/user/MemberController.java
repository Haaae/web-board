package toy.board.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import toy.board.constant.SessionConst;
import toy.board.controller.user.dto.request.*;
import toy.board.controller.user.dto.response.EmailVerificationResponse;
import toy.board.controller.user.dto.response.ExistResponse;
import toy.board.controller.user.dto.response.JoinResponse;
import toy.board.controller.user.dto.response.LoginResponse;
import toy.board.domain.user.Member;
import toy.board.repository.user.MemberRepository;
import toy.board.service.member.MemberService;

import java.util.Optional;

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
            @RequestBody @Valid final LoginRequest loginRequest,
            final HttpServletRequest request
    ) {
        // TODO: 2023-09-12 현재 로그인 여부 체크 로직 필요
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
    public ResponseEntity logout(final HttpServletRequest request) {

        request.getSession(false).invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity withdrawal(
            final HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        // 세션에서 사용자 정보 가져오기. 세션이 null이면 커스텀 예외 throws
        Long loginMemberId = (Long) session.getAttribute(
                SessionConst.LOGIN_MEMBER
        );

        memberService.withdrawal(loginMemberId);

        session.invalidate();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*  회원가입시 필요한 정보

        필수 정보
            - username(Login.class)
            - Password(Login.class)
            - Nickname(Profile.class)

     */
    @PostMapping
    public ResponseEntity<JoinResponse> join(@RequestBody @Valid final JoinRequest joinRequest) {
        Member member = memberService.join(joinRequest.username(), joinRequest.password(),
                joinRequest.nickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(JoinResponse.of(member));
    }

    @GetMapping("/usernames/{username}/exist")
    public ResponseEntity<ExistResponse> findUserByUsername(@PathVariable final String username) {
        Optional<Member> member = memberRepository.findMemberByUsername(username);

        return ResponseEntity.ok(
                new ExistResponse(member.isPresent())
        );
    }

    @GetMapping("/nicknames/{nickname}/exist")
    public ResponseEntity<ExistResponse> existNickname(@PathVariable final String nickname) {
        boolean isExists = memberRepository.existsByNickname(nickname);

        return ResponseEntity.ok(
                new ExistResponse(isExists)
        );
    }

    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestBody @Valid final SendEmailVerificationRequest request) {
        memberService.sendCodeToEmail(request.email());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/emails/verifications")
    public ResponseEntity<EmailVerificationResponse> verificationEmail(@RequestBody @Valid final EmailVerificationRequest request) {
        boolean result = memberService.verifiedCode(request.email(), request.authCode());

        return ResponseEntity.ok(EmailVerificationResponse.of(result));
    }

    @PostMapping("/roles/promotion")
    public ResponseEntity promoteRole(
            @RequestBody @Valid final RolePromotionDto rolePromotionDto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long masterId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);

        memberService.promoteMemberRole(masterId, rolePromotionDto.id());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
