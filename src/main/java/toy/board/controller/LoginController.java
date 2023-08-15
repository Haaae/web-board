package toy.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.board.service.LoginService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/local-login")
@Slf4j
public class LoginController {

    private final LoginService loginService;


}
