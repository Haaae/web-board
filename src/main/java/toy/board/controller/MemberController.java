package toy.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import toy.board.dto.RestResponse;
import toy.board.dto.login.LoginRequest;
import toy.board.entity.user.Member;
import toy.board.entity.user.UserRole;
import toy.board.service.LoginService;
import toy.board.service.MemberService;
import toy.board.session.SessionConst;

import java.util.Optional;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)  // @Autowired는 생성자가 한 개일 때 생략할 수 있음
public class MemberController {

    final MemberService memberService;
    final LoginService loginService;

    // Http Message Body를 객체에 매핑하는 @RequestBody는 객체의 프로퍼티가 하나라도 맞지 않으면 에러가 발생
    // Error Type: MethodArgumentNotValidException.class
    @PostMapping("/login")
    public ResponseEntity<RestResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            // TODO: 2023-08-02 DTO인 loginRequest의 어노테이션 유효성 검증 로직 구현
            HttpServletRequest request
    ) {

        // valid 실패 시 자동으로 에러 발생하므로 바로 member를 찾는다.
        Member loginMember = loginService.login(loginRequest.getId(), loginRequest.getPassword());

        // 찾은 member를 세션에 넣어준다.
        // 세션이 있으면 반환, 없으면 생성
        // JESSIONID는 톰캣이 자동으로 발급
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return ResponseEntity.ok(
                RestResponse.builder()
                        .success(true)
                        .message("로그인 성공")
                        .build()
        );
    }

    @DeleteMapping("/users/{userid}")
    public ResponseEntity<RestResponse> withdrawal(@PathVariable Long userid) {

        // TODO: 2023-08-03 session을 이용해서 구현해야 하는지 확인. 인프런 질문 올려놨음. 만약 그렇지 않고 지금이 맞다면 에러 클래스 생성
        Optional<Member> member = memberService.findMember(userid);

        // MemberService.findmember()는 withdrwal만 사용하는 것이 아니기 때문에 withdrwal()에 종속적인 예외를 발생시키기 어렵다.
        // 클라이언트 입력 오류로 인한 에러임을 명확히 다루기 위해서는 controller에서 다루는 것이 책임 측면에서 옳다고 판단.
        member.ifPresentOrElse(memberService::delete, IllegalArgumentException::new);

        return ResponseEntity.ok(
                RestResponse.builder()
                        .success(true)
                        .message("Delete Success")
                        .build()
        );
    }

    @PostMapping("/")
    public String join(JoinRequest signupRequest) {
        /*  회원가입시 필요한 정보

            필수 정보
                - ID(Login.class)
                - Password(Login.class)
                - Nickname(Profile.class)

            선택 입력 정보
                - 본인인증 정보

         */

        Member member = Member
                .builder()
                .localLogin(
                        LocalLogin
                                .builder()
                                .userId(signupRequest.getId())
                                .password(
                                        new BCryptPasswordEncoder().encode(signupRequest.getPassword())
                                )
                                .build()
                )
                .profile(Profile.builder().nickname(signupRequest.getNickname()).build())
                .role(UserRole.ADMIN)
                .build();

        memberRepository.save(member);

        return "";
    }
}
