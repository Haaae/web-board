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
import toy.board.controller.user.dto.request.JoinRequest;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class MemberControllerJoinTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String PREFIX_URL = "/users";
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
