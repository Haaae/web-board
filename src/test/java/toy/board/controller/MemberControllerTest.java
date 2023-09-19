package toy.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import toy.board.controller.user.dto.request.JoinRequest;
import toy.board.controller.user.dto.request.LoginRequest;
import toy.board.controller.user.dto.request.WithdrawalRequest;
import toy.board.domain.auth.Login;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;
import toy.board.repository.user.MemberRepository;
import toy.board.constant.SessionConst;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// 사용하려면 security test 라이브러리 등록해야 함

@Transactional
@ExtendWith(MockitoExtension.class) // Mockito와 같은 확장 기능을 테스트에 통합시켜주는 어노테이션
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc // controller뿐만 아니라 service와 repository 등의 컴포넌트도 mock으로 올린다.
//@WebMvcTest // controller만 mock으로 올림
//@Import(MemberTestConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String PREFIX_URL = "/users";
    private final String JOIN_URL = PREFIX_URL;
    private final String LOGOUT_URL = PREFIX_URL + "/logout";
    private final String LOGIN_URL = PREFIX_URL + "/login";
    private final String WITHDRAWAL_URL = PREFIX_URL;

    @DisplayName("회원탈퇴 성공")
    @Test
    public void withdrawal_success() throws  Exception {
        // given
        String username = "alsrbtls88@gmail.com";
        LoginType loginType = LoginType.LOCAL_LOGIN;
        UserRole userRole = UserRole.USER;
        String password = "password1!";
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = "asdas";

        Login login = new Login(encodedPassword);
        Profile profile = Profile.builder(nickname).build();
        Member member = Member.builder(username, login, profile, LoginType.LOCAL_LOGIN, UserRole.USER).build();
        member.changeLogin(login);

        memberRepository.save(member);
        session.setAttribute(SessionConst.LOGIN_MEMBER, member.getId());

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(password);
        String content = mapper.writeValueAsString(withdrawalRequest);

        // then
        mockMvc.perform(delete(WITHDRAWAL_URL).with(csrf())
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(MockMvcResultMatchers.status().isOk())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원탈퇴 실패: 세션에 저장된 id에 해당하는 객체가 없는 경우")
    @Test
    public void withdrawal_fail_cause_not_found() throws  Exception {
        // given
        String password = "password1!";

        session.setAttribute(SessionConst.LOGIN_MEMBER, 1L);

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(password);
        String content = mapper.writeValueAsString(withdrawalRequest);

        // then
        mockMvc.perform(delete(WITHDRAWAL_URL).with(csrf())
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(MockMvcResultMatchers.status().isNotFound())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원탈퇴 실패: 객체의 비밀번호와 입력 비밀번호가 다를 경우")
    @Test
    public void withdrawal_fail_cause_wrong_password() throws  Exception {
        // given
        String username = "alsrbtls88@gmail.com";
        LoginType loginType = LoginType.LOCAL_LOGIN;
        UserRole userRole = UserRole.USER;
        String password = "password1!";
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = "asfas";
        String wrongPassword = "wrong password";

        Login login = new Login(encodedPassword);
        Profile profile = Profile.builder(nickname).build();
        Member member = Member.builder(username, login, profile, LoginType.LOCAL_LOGIN, UserRole.USER).build();

        memberRepository.save(member);
        session.setAttribute(SessionConst.LOGIN_MEMBER, member.getId());

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(wrongPassword);
        String content = mapper.writeValueAsString(withdrawalRequest);

        // then
        mockMvc.perform(delete(WITHDRAWAL_URL).with(csrf())
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("login 성공: 요청한 로그인 정보와 동일한 member 객체가 존재할 경우")
    @Test
    public void login_success() throws  Exception {
        // given
        String username = "username1@gmail.com";
        LoginType loginType = LoginType.LOCAL_LOGIN;
        UserRole userRole = UserRole.USER;
        String password = "password1!";
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = "nickme2";

        Login login = new Login(encodedPassword);
        Profile profile = Profile.builder(nickname).build();
        Member member = Member.builder(username, login, profile, loginType, userRole).build();
        member.changeLogin(login);

        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest(username, password);
        String content = mapper.writeValueAsString(loginRequest);

        mockMvc.perform(post(LOGIN_URL).with(csrf())
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(MockMvcResultMatchers.status().isOk())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("login 실패: 요청한 로그인 정보와 동일한 member 객체가 존재하지 않을 경우")
    @Test
    public void login_fail_cause_no_exists_member() throws  Exception {
        // given
        String username = "name";
        LoginType loginType = LoginType.LOCAL_LOGIN;
        UserRole userRole = UserRole.USER;
        String password = "password1!";
        String nickname = "nicame1";

        Login login = new Login(password);
        Profile profile = Profile.builder(nickname).build();
        Member member = Member.builder(username, login, profile, LoginType.LOCAL_LOGIN, UserRole.USER).build();

        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest("username@gmail.com", "password1!");
        String content = mapper.writeValueAsString(loginRequest);

        mockMvc.perform(post(LOGIN_URL).with(csrf())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(MockMvcResultMatchers.status().isNotFound())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("logout 성공: seesion이 존재하는 경우")
    @Test
    public void logout_success() throws  Exception {
        session.setAttribute(SessionConst.LOGIN_MEMBER, 1L);

        mockMvc.perform(post(LOGOUT_URL).with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(MockMvcResultMatchers.status().isOk())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("logout 실패: seesion이 없는 경우")
    @Test
    public void logout_fail_not_exists_session() throws  Exception {

        mockMvc.perform(post(LOGOUT_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(MockMvcResultMatchers.status().isUnauthorized())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원가입 성공")
    @Test
    public void join_success() throws Exception {
        //given
        JoinRequest joinRequest = new JoinRequest("username@gmail.com", "password1!", "nickname");
        String content = mapper.writeValueAsString(joinRequest);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))

                .andExpect(MockMvcResultMatchers.status().isCreated())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원가입 실패: 이메일이 형식에 맞지 않음")
    @Test
    public void join_fail_cause_wrong_pattern_username() throws  Exception {
        //given
        JoinRequest joinRequest = new JoinRequest("usernamegmail.com", "password1!", "nickname");
        String content = mapper.writeValueAsString(joinRequest);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원가입 실패: 비밀번호가 형식에 맞지 않음")
    @Test
    public void join_fail_cause_wrong_pattern_password() throws  Exception {
        //given
        JoinRequest joinRequest1 = new JoinRequest("username@gmail.com", "password1", "nickname");
        String content1 = mapper.writeValueAsString(joinRequest1);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content1)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());

        //given
        JoinRequest joinRequest2 = new JoinRequest("username@gmail.com", "!", "nickname");
        String content2 = mapper.writeValueAsString(joinRequest2);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content2)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());

        //given
        JoinRequest joinRequest3 = new JoinRequest("username@gmail.com", "ㅁㄴㅇㄻㄴㅇㄹ", "nickname");
        String content3 = mapper.writeValueAsString(joinRequest3);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content3)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원가입 실패: 이메일이 전달되지 않음")
    @Test
    public void join_fail_cause_empty_username() throws  Exception {
        //given
        JoinRequest joinRequest1 = new JoinRequest("", "password1", "nickname");
        String content1 = mapper.writeValueAsString(joinRequest1);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content1)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());

        //given
        JoinRequest joinRequest2 = new JoinRequest(null, "password1", "nickname");
        String content2 = mapper.writeValueAsString(joinRequest2);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content2)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원가입 실패: 비밀번호가 전달되지 않음")
    @Test
    public void join_fail_cause_empty_password() throws  Exception {
        //given
        JoinRequest joinRequest1 = new JoinRequest("alssdf33@gmail.com", "", "nickname");
        String content1 = mapper.writeValueAsString(joinRequest1);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content1)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());

        //given
        JoinRequest joinRequest2 = new JoinRequest("alsrbtls88@gmail.com", null, "nickname");
        String content2 = mapper.writeValueAsString(joinRequest2);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content2)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원가입 실패: 닉네임이 전달되지 않음")
    @Test
    public void join_fail_cause_empty_nickname() throws  Exception {
        //given
        JoinRequest joinRequest1 = new JoinRequest("alssdf33@gmail.com", "password", "");
        String content1 = mapper.writeValueAsString(joinRequest1);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content1)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());

        //given
        JoinRequest joinRequest2 = new JoinRequest("alsrbtls88@gmail.com", "password", null);
        String content2 = mapper.writeValueAsString(joinRequest2);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content2)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원가입 실패: 닉네임 길이가 형식에 맞지 않음")
    @Test
    public void join_fail_cause_size_miss_nickname() throws  Exception {
        //given
        JoinRequest joinRequest1 = new JoinRequest("alssdf33@gmail.com", "password", "s");
        String content1 = mapper.writeValueAsString(joinRequest1);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content1)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());

        //given
        JoinRequest joinRequest2 = new JoinRequest("alsrbtls88@gmail.com", "password", "sdfsdfsdf");
        String content2 = mapper.writeValueAsString(joinRequest2);

        // then
        mockMvc.perform(post(JOIN_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content2)
                )

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andDo(MockMvcResultHandlers.print());
    }
}