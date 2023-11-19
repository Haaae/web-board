package toy.board.controller.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

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
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.repository.user.MemberRepository;

@Transactional
@ExtendWith(MockitoExtension.class) // Mockito와 같은 확장 기능을 테스트에 통합시켜주는 어노테이션
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc // controller뿐만 아니라 service와 repository 등의 컴포넌트도 mock으로 올린다.
//@WebMvcTest // controller만 mock으로 올림
//@Import(MemberTestConfig.class)
public class MemberControllerWithdrawalTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private final MockHttpSession session = new MockHttpSession();
    private final String PREFIX_URL = "/users";
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
