package toy.board.controller.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import toy.board.constant.SessionConst;
import toy.board.controller.user.dto.request.RolePromotionRequest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.exception.ErrorResponse;
import toy.board.exception.ExceptionCode;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class RoleControllerTest {

    @Autowired
    EntityManager em;
    @Autowired
    private MockMvc mockMvc;

    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();


    @DisplayName("권한 변경 성공")
    @Test
    void 사용자_권한변경_성공시_권한_ADMIN으로_변경됨() throws Exception {
        //given
        Member target = MemberTest.create(
                "target",
                "target",
                UserRole.USER
        );
        Member master = MemberTest.create(
                "master",
                "master",
                UserRole.MASTER
        );

        em.persist(target);
        em.persist(master);

        Long targetId = target.getId();
        Long masterId = master.getId();

        //when
        session.setAttribute(SessionConst.LOGIN_MEMBER, masterId);

        RolePromotionRequest request = new RolePromotionRequest(targetId);
        String content = mapper.writeValueAsString(request);
        String url = "/users/roles/promotion";

        //then
        UserRole exceptedTargetRole = UserRole.ADMIN;

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

        Assertions.assertThat(target.getRole())
                .isEqualTo(exceptedTargetRole);
    }

    @DisplayName("권한 변경 실패 : Session이 존재하지 않으면 요청실패")
    @Test
    void session이_존재하지_않으면_요청실패() throws Exception {
        //given
        Member target = MemberTest.create(
                "target",
                "target",
                UserRole.USER
        );

        em.persist(target);
        Long targetId = target.getId();

        //when
        RolePromotionRequest request = new RolePromotionRequest(targetId);
        String content = mapper.writeValueAsString(request);
        String url = "/users/roles/promotion";

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
                                .isUnauthorized()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                )
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);

        String expectedErrorCode = ExceptionCode.BAD_REQUEST_AUTHENTICATION.getCode();

        Assertions.assertThat(errorResponse.code())
                .isEqualTo(expectedErrorCode);
    }

    @DisplayName("권한 변경 실패 : 자격 승급을 요청하는 사용자의 권한이 Master가 아니면 요청실패")
    @Test
    void 자격승급_요청_사용자의_권한이_master가_아니면_요청실패() throws Exception {
        //given
        Member target = MemberTest.create(
                "target",
                "target",
                UserRole.USER
        );
        Member userRoleMember = MemberTest.create(
                "user",
                "user",
                UserRole.USER
        );
        Member adminRoleMember = MemberTest.create(
                "admin",
                "admin",
                UserRole.USER
        );

        em.persist(target);
        em.persist(userRoleMember);
        em.persist(adminRoleMember);
        Long targetId = target.getId();
        Long userRoleMemberId = userRoleMember.getId();
        Long adminRoleMemberId = adminRoleMember.getId();

        // 사용자의 권한이 user, admin인 경우
        List<Long> memberIds = List.of(userRoleMemberId, adminRoleMemberId);

        for (Long memberId : memberIds) {
            //when
            session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);

            RolePromotionRequest request = new RolePromotionRequest(targetId);
            String content = mapper.writeValueAsString(request);
            String url = "/users/roles/promotion";

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
                                    .isForbidden()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    )
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);

            String expectedErrorCode = ExceptionCode.INVALID_AUTHORITY.getCode();

            Assertions.assertThat(errorResponse.code())
                    .isEqualTo(expectedErrorCode);
        }
    }

    @DisplayName("권한 변경 실패 : 자격 승급 대상 회원을 찾을 수 없으면 요청실패")
    @Test
    void 자격_승급_대상_회원을_찾을_수_없으면_요청실패() throws Exception {
        //given
        long notExistMemberId = Math.abs(random.nextLong());

        Member master = MemberTest.create(
                "user",
                "user",
                UserRole.MASTER
        );

        em.persist(master);
        Long masterId = master.getId();

        //when
        session.setAttribute(SessionConst.LOGIN_MEMBER, masterId);

        RolePromotionRequest request = new RolePromotionRequest(notExistMemberId);
        String content = mapper.writeValueAsString(request);
        String url = "/users/roles/promotion";

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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                )
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);

        String expectedErrorCode = ExceptionCode.NOT_FOUND.getCode();

        Assertions.assertThat(errorResponse.code())
                .isEqualTo(expectedErrorCode);
    }
}