package toy.board.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import toy.board.constant.SessionConst;
import toy.board.controller.user.dto.request.JoinRequest;
import toy.board.controller.user.dto.request.LoginRequest;
import toy.board.controller.user.dto.response.ExistResponse;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.repository.user.MemberRepository;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc // controller뿐만 아니라 service와 repository 등의 컴포넌트도 mock으로 올린다.
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private final MockHttpSession session = new MockHttpSession();
    private final String PREFIX_URL = "/users";
    private final ObjectMapper mapper = new ObjectMapper();


    @Nested
    class WithdrawalTest {
        private final String WITHDRAWAL_URL = PREFIX_URL;

        @DisplayName("회원탈퇴 성공")
        @Test
        public void withdrawal_success() throws Exception {
            // given
            Member member = MemberTest.create(
                    "username",
                    "nickname",
                    UserRole.USER
            );
            memberRepository.save(member);
            session.setAttribute(SessionConst.LOGIN_MEMBER, member.getId());

            // then
            mockMvc.perform(
                            delete(WITHDRAWAL_URL)
                                    .session(session)
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

        @DisplayName("회원탈퇴 실패: 세션에 저장된 id에 해당하는 객체가 없는 경우")
        @Test
        public void withdrawal_fail_cause_not_found() throws Exception {
            // given
            long invalidMemberId = 1L;
            session.setAttribute(SessionConst.LOGIN_MEMBER, invalidMemberId);

            // then
            mockMvc.perform(delete(WITHDRAWAL_URL).with(csrf())
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                    )

                    .andExpect(MockMvcResultMatchers.status().isBadRequest())

                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Nested
    class JoinTest {
        private final String JOIN_URL = PREFIX_URL;


        @DisplayName("회원가입 성공")
        @Test
        public void join_success() throws Exception {
            //given
            JoinRequest joinRequest = new JoinRequest
                    ("username@gmail.com",
                            "password1!",
                            "nickname"
                    );
            String content = mapper.writeValueAsString(joinRequest);

            // then
            mockMvc.perform(
                            post(JOIN_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))

                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isCreated()
                    )

                    .andDo(
                            MockMvcResultHandlers.print()
                    );
        }

        @DisplayName("회원가입 실패: 이메일이 형식에 맞지 않음")
        @Test
        public void join_fail_cause_wrong_pattern_username() throws Exception {
            //given
            JoinRequest joinRequest = new JoinRequest(
                    "usernamegmail.com",
                    "password1!",
                    "nickname"
            );
            String content = mapper.writeValueAsString(joinRequest);

            // then
            assertThatJoinApiFail(content);
        }

        @DisplayName("회원가입 실패: 비밀번호가 형식에 맞지 않음")
        @Test
        public void join_fail_cause_wrong_pattern_password() throws Exception {
            //given
            JoinRequest joinRequest = new JoinRequest(
                    "username@gmail.com",
                    "password1",
                    "nickname");
            String content = mapper.writeValueAsString(joinRequest);

            JoinRequest joinRequest2 = new JoinRequest(
                    "username@gmail.com",
                    "!",
                    "nickname"
            );
            String content2 = mapper.writeValueAsString(joinRequest2);

            JoinRequest joinRequest3 = new JoinRequest(
                    "username@gmail.com",
                    "ㅁㄴㅇㄻㄴㅇㄹ",
                    "nickname"
            );
            String content3 = mapper.writeValueAsString(joinRequest3);

            // then
            assertThatJoinApiFail(content);
            assertThatJoinApiFail(content2);
            assertThatJoinApiFail(content3);
        }

        @DisplayName("회원가입 실패: 이메일이 전달되지 않음")
        @Test
        public void join_fail_cause_empty_username() throws Exception {
            //given
            JoinRequest joinRequest1 = new JoinRequest(
                    "",
                    "password1",
                    "nickname"
            );
            String content1 = mapper.writeValueAsString(joinRequest1);

            JoinRequest joinRequest2 = new JoinRequest(
                    null,
                    "password1",
                    "nickname"
            );
            String content2 = mapper.writeValueAsString(joinRequest2);

            // then
            assertThatJoinApiFail(content1);
            assertThatJoinApiFail(content2);
        }

        @DisplayName("회원가입 실패: 비밀번호가 전달되지 않음")
        @Test
        public void join_fail_cause_empty_password() throws Exception {
            //given
            JoinRequest joinRequest1 = new JoinRequest(
                    "alssdf33@gmail.com",
                    "",
                    "nickname"
            );
            String content1 = mapper.writeValueAsString(joinRequest1);

            JoinRequest joinRequest2 = new JoinRequest(
                    "alsrbtls88@gmail.com",
                    null,
                    "nickname"
            );
            String content2 = mapper.writeValueAsString(joinRequest2);

            // then
            assertThatJoinApiFail(content1);
            assertThatJoinApiFail(content2);
        }

        @DisplayName("회원가입 실패: 닉네임이 전달되지 않음")
        @Test
        public void join_fail_cause_empty_nickname() throws Exception {
            //given
            JoinRequest joinRequest1 = new JoinRequest(
                    "alssdf33@gmail.com",
                    "password",
                    ""
            );
            String content1 = mapper.writeValueAsString(joinRequest1);

            JoinRequest joinRequest2 = new JoinRequest(
                    "alsrbtls88@gmail.com",
                    "password",
                    null
            );
            String content2 = mapper.writeValueAsString(joinRequest2);

            // then
            assertThatJoinApiFail(content1);
            assertThatJoinApiFail(content2);
        }

        @DisplayName("회원가입 실패: 닉네임 길이가 형식에 맞지 않음")
        @Test
        public void join_fail_cause_size_miss_nickname() throws Exception {
            //given
            JoinRequest joinRequest1 = new JoinRequest(
                    "alssdf33@gmail.com",
                    "password",
                    "s"
            );
            String content1 = mapper.writeValueAsString(joinRequest1);

            JoinRequest joinRequest2 = new JoinRequest(
                    "alsrbtls88@gmail.com",
                    "password",
                    "sdfsdfsdf"
            );
            String content2 = mapper.writeValueAsString(joinRequest2);
            // then
            assertThatJoinApiFail(content1);
            assertThatJoinApiFail(content2);
        }

        private void assertThatJoinApiFail(String content) throws Exception {
            mockMvc.perform(
                            post(JOIN_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content)
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

    @Nested
    class LoginTest {

        private final String LOGIN_URL = PREFIX_URL + "/login";

        @DisplayName("login 성공: 요청한 로그인 정보와 동일한 member 객체가 존재할 경우")
        @Test
        public void login_success() throws Exception {
            // given
            String username = "username1@gmail.com";
            String password = "password1!";

            Member member = new Member(
                    username,
                    "nickname",
                    passwordEncoder.encode(password),
                    UserRole.USER
            );

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

    @Nested
    class LogoutTest {
        private final String LOGOUT_URL = PREFIX_URL + "/logout";

        @DisplayName("logout 성공: seesion이 존재하는 경우")
        @Test
        public void logout_success() throws Exception {
            session.setAttribute(SessionConst.LOGIN_MEMBER, 1L);

            mockMvc.perform(
                            post(LOGOUT_URL)
                                    .session(session)
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

        @DisplayName("logout 실패: seesion이 없는 경우")
        @Test
        public void logout_fail_not_exists_session() throws Exception {

            mockMvc.perform(
                            post(LOGOUT_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )

                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isUnauthorized()
                    )

                    .andDo(
                            MockMvcResultHandlers.print()
                    );
        }
    }

    @Nested
    class CheckTest {

        @DisplayName("username 존재 확인 성공 : username이 존재하면 true 반환")
        @Test
        public void username_존재하면_true_반환() throws Exception {
            // given
            String username = "username1@gmail.com";
            String password = "password1!";

            Member member = new Member(
                    username,
                    "nickname",
                    passwordEncoder.encode(password),
                    UserRole.USER
            );

            memberRepository.save(member);

            String path = PREFIX_URL + "/usernames/" + username + "/exist";

            MvcResult mvcResult = mockMvc.perform(
                            get(path)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isOk()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    )
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            ExistResponse response = mapper.readValue(contentAsString, ExistResponse.class);
            assertThat(response.exist()).isTrue();
        }

        @DisplayName("username 존재 확인 성공 : username이 존재하지 않으면 false 반환")
        @Test
        public void username_존재하지_않으면_false_반환() throws Exception {
            // given
            String username = "username1@gmail.com";

            String path = PREFIX_URL + "/usernames/" + username + "/exist";

            MvcResult mvcResult = mockMvc.perform(
                            get(path)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isOk()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    )
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            ExistResponse response = mapper.readValue(contentAsString, ExistResponse.class);
            assertThat(response.exist()).isFalse();
        }
    }

    @DisplayName("nickname 존재 확인 성공 : nickname이 존재하면 true 반환")
    @Test
    public void nickname_존재하면_true_반환() throws Exception {
        // given
        String nickname = "nickname";

        Member member = new Member(
                "username1@gmail.com",
                "nickname",
                passwordEncoder.encode("password1!"),
                UserRole.USER
        );

        memberRepository.save(member);

        String path = PREFIX_URL + "/nicknames/" + nickname + "/exist";

        MvcResult mvcResult = mockMvc.perform(
                        get(path)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isOk()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                )
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ExistResponse response = mapper.readValue(contentAsString, ExistResponse.class);
        assertThat(response.exist()).isTrue();
    }

    @DisplayName("nickname 존재 확인 성공 : nickname이 존재하지 않으면 false 반환")
    @Test
    public void nickname_존재하지_않으면_false_반환() throws Exception {
        // given
        String nickname = "nickname";

        String path = PREFIX_URL + "/nicknames/" + nickname + "/exist";

        MvcResult mvcResult = mockMvc.perform(
                        get(path)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isOk()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                )
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ExistResponse response = mapper.readValue(contentAsString, ExistResponse.class);
        assertThat(response.exist()).isFalse();
    }
}
