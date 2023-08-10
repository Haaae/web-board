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
import toy.board.dto.RestResponse;
import toy.board.dto.login.*;
import toy.board.dto.user.FindUserResponse;
import toy.board.entity.user.Member;
import toy.board.exception.NoExistMemberById;
import toy.board.exception.NoExistMemberByNickname;
import toy.board.exception.NoExistSession;
import toy.board.exception.NotLoginException;
import toy.board.exception.login.NoExistMemberByUsername;
import toy.board.repository.MemberRepository;
import toy.board.service.LoginService;
import toy.board.service.MemberService;
import toy.board.session.SessionConst;

import java.util.Objects;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)  // @Autowired는 생성자가 한 개일 때 생략할 수 있음
public class MemberController {

    private static final String LOGIN_SUCCESS_MESSAGE = "Login Success";
    private static final String LOGOUT_SUCCESS_MESSAGE = "Logout Success";
    private static final String DELETE_SUCCESS_MESSAGE = "Delete Success";
    private static final String JOIN_SUCCESS_MESSAGE = "Join Success";
    public static final String USER_BY_USERNAME_MESSAGE = "Find User By Username Success";
    public static final String USER_BY_NICKNAME_MESSAGE = "Find User By Nick Success";

    private final MemberService memberService;
    private final LoginService loginService;
    private final MemberRepository memberRepository;

    // Http Message Body를 객체에 매핑하는 @RequestBody는 객체의 프로퍼티가 하나라도 맞지 않으면 에러가 발생
    // Error Type: MethodArgumentNotValidException.class
    @PostMapping("/login")
    public ResponseEntity<RestResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            // TODO: 2023-08-02 DTO인 loginRequest의 어노테이션 유효성 검증 로직 구현
            HttpServletRequest request
    ) {

        // TODO: 2023-08-10 test

        // valid 실패 시 자동으로 에러 발생하므로 바로 member를 찾는다.
        Member loginMember = loginService.login(loginRequest.getUsername(),
                loginRequest.getPassword());

        // 찾은 member를 세션에 넣어준다.
        // 세션이 있으면 반환, 없으면 생성
        // JESSIONID는 톰캣이 자동으로 발급
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember.getId());

        return RestResponse.createWithResponseEntity(HttpStatus.OK, true, LOGIN_SUCCESS_MESSAGE,
                null);
    }

    @PostMapping("/logout")
    public ResponseEntity<RestResponse> logout(HttpServletRequest request) {
        String field = "logout";

        try {
            Objects.requireNonNull(request.getSession(false)).invalidate();
            return RestResponse.createWithResponseEntity(HttpStatus.OK, true,
                    LOGOUT_SUCCESS_MESSAGE, null);
        } catch (NullPointerException ex) {
            // session이 없는 경우
            throw new NoExistSession(field);
        }
    }

    @DeleteMapping
    public ResponseEntity<RestResponse> withdrawal(
            @RequestBody @Valid WithdrawalRequest withdrawalRequest,
            HttpServletRequest request
    ) {
        String field = "withdrawal";

        // TODO: 2023-08-07 세션 여부, 즉 인증과 인가에 대한 부분을 AOS(혹은 필터)로 분리하여 공통처리할 것
        // TODO: 2023-08-10 test

        // 세션이 존재하는지 확인. 요청에 세션이 없다면 null.
        HttpSession session = request.getSession(false);

        // 세션에서 사용자 정보 가져오기. 세션이 null이면 커스텀 예외 throws
        try {
            Long loginMemberId = (Long) Objects.requireNonNull(session)
                    .getAttribute(SessionConst.LOGIN_MEMBER);

            memberService.delete(loginMemberId);

            return RestResponse.createWithResponseEntity(HttpStatus.OK, true,
                    DELETE_SUCCESS_MESSAGE, null);
        } catch (NullPointerException ex) {
            // session이 없는 경우
            throw new NoExistSession(field);
        } catch (IllegalStateException ex) {
            // session에 회원 로그인 정보가 없는 경우
            throw new NotLoginException(field);
        } catch (IllegalArgumentException ex) {
            // memberId에 맞는 회원이 db에서 조회되지 않을 경우
            throw new NoExistMemberById(field);
        }
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
    public ResponseEntity<RestResponse> join(@RequestBody @Valid JoinRequest joinRequest) {

        // TODO: 2023-08-10 test

        Member member = memberService.join(joinRequest.username(), joinRequest.password(),
                joinRequest.nickname());

        return RestResponse.createWithResponseEntity(HttpStatus.CREATED, true, JOIN_SUCCESS_MESSAGE,
                JoinResponse.of(member));
    }

    @GetMapping("/usernames/{username}")
    public ResponseEntity<RestResponse> findUserByUsername(@PathVariable String username) {
        // TODO: 2023-08-10 test 
        Optional<Member> member = memberRepository.findMemberByUsername(username);

        return RestResponse.createWithResponseEntity(
                HttpStatus.OK,
                true,
                USER_BY_USERNAME_MESSAGE,
                FindUserResponse.of(
                        member.orElseThrow(() -> new NoExistMemberByUsername("find user by username")))
        );
    }

    @GetMapping("/nicknames/{nickname}")
    public ResponseEntity<RestResponse> findUserByNickname(@PathVariable String nickname) {
        // TODO: 2023-08-10 test
        Optional<Member> member = memberRepository.findMemberByNickname(nickname);

        return RestResponse.createWithResponseEntity(
                HttpStatus.OK,
                true,
                USER_BY_NICKNAME_MESSAGE,
                FindUserResponse.of(
                        member.orElseThrow(() -> new NoExistMemberByNickname("find user by nickname")))
        );
    }
}
