package toy.board.controller.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import toy.board.controller.email.dto.request.EmailVerificationRequest;
import toy.board.controller.email.dto.request.SendEmailVerificationRequest;
import toy.board.controller.email.dto.response.EmailVerificationResponse;
import toy.board.service.mail.MailService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MailService mailService;

    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    @DisplayName("이메일 검증 코드 발송 성공 : 기존에 없는 email인 경우 발송 성공")
    @Test
    void 이메일_검증_코드_발송_성공() throws Exception {
        //given
        String email = "email@email.com";

        //when
        SendEmailVerificationRequest request = new SendEmailVerificationRequest(email);
        String content = mapper.writeValueAsString(request);
        String url = "/users/emails/verification-requests";

        doNothing()
                .when(mailService)
                .sendCodeToEmail(eq(email));

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(url)
                                .session(session)
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

    @DisplayName("입력 코드 검증 성공 : 입력 코드가 일치할 경우 true 반환")
    @Test
    void 입력__코드_검증_성공() throws Exception {
        //given
        String email = "email@email.com";
        String authCode = "123123";
        boolean result = true;
        //when
        EmailVerificationRequest request = new EmailVerificationRequest(email, authCode);
        String content = mapper.writeValueAsString(request);
        String url = "/users/emails/verifications";

        given(
                mailService.verifiedCode(
                        eq(email),
                        eq(authCode)
                )
        )
                .willReturn(result);

        //then
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(url)
                                .session(session)
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
                )
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        EmailVerificationResponse response = mapper.readValue(
                contentAsString,
                EmailVerificationResponse.class
        );

        assertThat(response.isCertificated()).isTrue();
    }
}