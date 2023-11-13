package toy.board.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import toy.board.constant.SessionConst;
import toy.board.controller.post.dto.request.PostCreationRequest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PostCreationControllerTest {

    public static final String POST_URL = "/posts";

    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    private String tooLongContent = "contentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentconten";

    @DisplayName("게시물 생성: 세션 없으면 실패")
    @Test
    public void whenCreatePostWithNoSession_thenUnauthorizedFail() throws Exception {
        //given
        //when
        PostCreationRequest request = new PostCreationRequest("title", "content");
        String content = mapper.writeValueAsString(request);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(POST_URL)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 생성 실패: too long content")
    @Test
    public void whenCreatePostWithTooLongContent_thenFail() throws Exception {
        //given
        Member member = persistNewMember();
        Long memberId = member.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostCreationRequest request = new PostCreationRequest("title",
                tooLongContent + tooLongContent + tooLongContent +
                        tooLongContent + tooLongContent + tooLongContent +
                        tooLongContent + tooLongContent + tooLongContent + tooLongContent);
        String content = mapper.writeValueAsString(request);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(POST_URL)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 생성 실패: too long title")
    @Test
    public void whenCreatePostWithTooLongTitle_thenFail() throws Exception {
        //given
        Member member = persistNewMember();
        Long memberId = member.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostCreationRequest request = new PostCreationRequest(tooLongContent, "content");
        String content = mapper.writeValueAsString(request);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(POST_URL)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 생성 실패: empty content")
    @Test
    public void whenCreatePostWithEmptyContent_thenFail() throws Exception {
        //given
        Member member = persistNewMember();
        Long memberId = member.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostCreationRequest request = new PostCreationRequest("title", "");
        String content = mapper.writeValueAsString(request);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(POST_URL)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 생성 실패: empty title")
    @Test
    public void whenCreatePostWithEmptyTitle_thenFail() throws Exception {
        //given
        Member member = persistNewMember();
        Long memberId = member.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostCreationRequest request = new PostCreationRequest("", "content");
        String content = mapper.writeValueAsString(request);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(POST_URL)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 생성: 세션 있으면 성공")
    @Test
    public void whenCreatePostWithSession_thenUnauthorizedFail() throws Exception {
        //given
        Member member = persistNewMember();
        Long memberId = member.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostCreationRequest request = new PostCreationRequest("title", "content");
        String content = mapper.writeValueAsString(request);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(POST_URL)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    private Member persistNewMember() {
        Member member = MemberTest.create("username", "nickname", UserRole.USER);
        em.persist(member);
        return member;
    }
}
