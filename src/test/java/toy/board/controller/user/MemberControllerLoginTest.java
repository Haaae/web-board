package toy.board.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import toy.board.controller.user.dto.request.LoginRequest;
import toy.board.domain.auth.Login;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;
import toy.board.repository.user.MemberRepository;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class MemberControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String PREFIX_URL = "/users";
    private final String LOGIN_URL = PREFIX_URL + "/login";

    @DisplayName("login 성공: 요청한 로그인 정보와 동일한 member 객체가 존재할 경우")
    @Test
    public void login_success() throws Exception {
        // given
        String username = "username1@gmail.com";
        String password = "password1!";

        Member member = Member.builder(
                username,
                new Login(
                        passwordEncoder.encode(password)
                ),
                Profile.builder(
                        "nickname"
                ).build(),
                LoginType.LOCAL_LOGIN,
                UserRole.USER
        ).build();

        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest(username, password);
        String content = mapper.writeValueAsString(loginRequest);

        mockMvc.perform(
                        post(LOGIN_URL)
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isOk()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("login 실패: 요청한 로그인 정보와 동일한 member 객체가 존재하지 않을 경우")
    @Test
    public void login_fail_cause_no_exists_member() throws Exception {
        // given
        String invalidUsername = "username1@gmail.com";

        Member member = MemberTest.create(
                "username",
                "nickname",
                UserRole.USER
        );
        String password = member.getPassword();
        memberRepository.save(member);

        //when
        LoginRequest loginRequest = new LoginRequest(invalidUsername, "password1!");
        String content = mapper.writeValueAsString(loginRequest);

        //then
        mockMvc.perform(
                        post(LOGIN_URL)
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }
}
