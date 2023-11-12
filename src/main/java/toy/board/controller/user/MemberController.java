package toy.board.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.constant.SessionConst;
import toy.board.controller.api.response.annotation.ApiAuthenticationError;
import toy.board.controller.api.response.annotation.ApiBadRequestArgError;
import toy.board.controller.api.response.annotation.ApiDuplicationError;
import toy.board.controller.api.response.annotation.ApiFoundError;
import toy.board.controller.api.response.annotation.ApiLoginTypeError;
import toy.board.controller.user.dto.request.JoinRequest;
import toy.board.controller.user.dto.request.LoginRequest;
import toy.board.controller.user.dto.response.ExistResponse;
import toy.board.controller.user.dto.response.JoinResponse;
import toy.board.controller.user.dto.response.LoginResponse;
import toy.board.domain.user.Member;
import toy.board.repository.user.MemberRepository;
import toy.board.service.member.MemberService;

@Tag(name = "Member", description = "Member API Document")
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)  // @Autowired는 생성자가 한 개일 때 생략할 수 있음
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // Http Message Body를 객체에 매핑하는 @RequestBody는 객체의 프로퍼티가 하나라도 맞지 않으면 에러가 발생
    // Error Type: MethodArgumentNotValidException.class
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = LoginResponse.class
                    )
            )
    )
    @ApiBadRequestArgError
    @ApiAuthenticationError
    @ApiLoginTypeError
    @Operation(summary = "로그인", description = "로그인을 시도합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid final LoginRequest loginRequest,
            final HttpServletRequest request
    ) {
        // valid 실패 시 자동으로 에러 발생하므로 바로 member를 찾는다.
        Member loginMember = memberService.login(
                loginRequest.username(),
                loginRequest.password()
        );

        // 찾은 member를 세션에 넣어준다. 세션이 있으면 반환, 없으면 생성.
        // JESSIONID는 톰캣이 자동으로 발급
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember.getId());

        return ResponseEntity.ok(
                LoginResponse.of(loginMember)
        );
    }

    @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공"
    )
    @ApiAuthenticationError
    @Operation(summary = "로그아웃", description = "로그아웃을 시도합니다.")
    @PostMapping("/logout")
    public ResponseEntity logout(final HttpServletRequest request) {

        request.getSession(false).invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponse(
            responseCode = "200",
            description = "회원탈퇴 성공"
    )
    @ApiAuthenticationError
    @ApiFoundError
    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 시도합니다.")
    @DeleteMapping
    public ResponseEntity withdrawal(final HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        Long loginMemberId = (Long) session.getAttribute(
                SessionConst.LOGIN_MEMBER
        );

        memberService.withdrawal(loginMemberId);

        session.invalidate();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = JoinResponse.class
                    )
            )
    )
    @ApiDuplicationError
    @ApiBadRequestArgError
    @Operation(summary = "회원가입", description = "회원가입을 시도합니다.")
    @PostMapping
    public ResponseEntity<JoinResponse> join(@RequestBody @Valid final JoinRequest joinRequest) {

        Member member = memberService.join(
                joinRequest.username(),
                joinRequest.password(),
                joinRequest.nickname()
        );

        return ResponseEntity.status(
                        HttpStatus.CREATED
                )
                .body(
                        JoinResponse.of(member)
                );
    }

    @ApiResponse(
            responseCode = "200",
            description = "이메일로 사용자 존재 여부 확인 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ExistResponse.class
                    )
            )
    )
    @Operation(summary = "사용자 존재 여부 확인", description = "이메일로 사용자 존재 여부를 확인합니다.")
    @Parameter(name = "username", description = "존재 여부를 확인할 사용자 이메일")
    @GetMapping("/usernames/{username}/exist")
    public ResponseEntity<ExistResponse> findUserByUsername(@PathVariable final String username) {
        boolean isExists = memberRepository.existsByUsername(username);
        return ResponseEntity.ok(
                new ExistResponse(isExists)
        );
    }

    @ApiResponse(
            responseCode = "200",
            description = "닉네임으로 사용자 존재 여부 확인 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ExistResponse.class
                    )
            )
    )
    @ApiAuthenticationError
    @Operation(summary = "사용자 존재 여부 확인", description = "닉네임으로 사용자 존재 여부를 확인합니다.")
    @Parameter(name = "nickname", description = "존재 여부를 확인할 사용자 이메일")
    @GetMapping("/nicknames/{nickname}/exist")
    public ResponseEntity<ExistResponse> existNickname(@PathVariable final String nickname) {
        boolean isExists = memberRepository.existsByNickname(nickname);
        return ResponseEntity.ok(
                new ExistResponse(isExists)
        );
    }
}
