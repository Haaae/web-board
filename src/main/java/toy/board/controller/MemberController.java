package toy.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.dto.RestResponse;
import toy.board.dto.login.LoginRequest;
import toy.board.entity.user.Member;
import toy.board.service.LoginService;
import toy.board.session.SessionConst;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)  // @Autowired는 생성자가 한 개일 때 생략할 수 있음
public class MemberController {

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

//    @PostMapping("/signup")
//    public String create(SignupRequest signupRequest) {
//        /*  회원가입시 필요한 정보
//
//            필수 정보
//                - ID(Login.class)
//                - Password(Login.class)
//                - Nickname(Profile.class)
//
//            선택 입력 정보
//                - 본인인증 정보
//
//         */
//
//        Member member = Member
//                .builder()
//                .localLogin(
//                        LocalLogin
//                                .builder()
//                                .userId(signupRequest.getId())
//                                .password(
//                                        new BCryptPasswordEncoder().encode(signupRequest.getPassword())
//                                )
//                                .build()
//                )
//                .profile(Profile.builder().nickname(signupRequest.getNickname()).build())
//                .role(UserRole.ADMIN)
//                .build();
//
//        memberRepository.save(member);
//
//        return "";
//    }
}
