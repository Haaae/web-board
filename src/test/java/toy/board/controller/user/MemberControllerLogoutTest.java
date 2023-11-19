package toy.board.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
import toy.board.constant.SessionConst;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MemberControllerLogoutTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    private final MockHttpSession session = new MockHttpSession();
    private final String PREFIX_URL = "/users";
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