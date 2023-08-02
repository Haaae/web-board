package toy.board.domain.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.board.domain.login.dto.LocalLoginRequest;
import toy.board.domain.login.entity.user.Member;
import toy.board.domain.login.service.LocalLoginService;
import toy.board.dto.RestResponse;
import toy.board.session.SessionConst;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/local-login")
@Slf4j
public class LocalLoginController {

    private final LocalLoginService localLoginService;


    // Http Message Body를 객체에 매핑하는 @RequestBody는 객체의 프로퍼티가 하나라도 맞지 않으면 에러가 발생
    // Error Type: MethodArgumentNotValidException.class
    @PostMapping("/")
    public ResponseEntity<RestResponse> localLogin(
            @RequestBody @Valid LocalLoginRequest loginRequest,
            HttpServletRequest request
    ) {

        // valid 실패 시 자동으로 에러 발생하므로 바로 member를 찾는다.
        Member loginMember = localLoginService.login(loginRequest.getId(), loginRequest.getPassword());

        // 찾은 member를 세션에 넣어준다.
        // 세션이 있으면 반환, 없으면 생성
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return ResponseEntity.ok(
                RestResponse.builder()
                        .success(true)
                        .message("로그인 성공")
                        .build()
        );
    }
}
