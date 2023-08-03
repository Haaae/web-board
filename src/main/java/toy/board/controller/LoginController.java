package toy.board.controller;

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
import toy.board.dto.login.LoginRequest;
import toy.board.entity.user.Member;
import toy.board.service.LoginService;
import toy.board.dto.RestResponse;
import toy.board.session.SessionConst;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/local-login")
@Slf4j
public class LoginController {

    private final LoginService loginService;


}
