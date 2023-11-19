package toy.board.controller.post;

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
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PostDeleteControllerTest {

    public static final String POST_URL = "/posts";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    private final MockHttpSession session = new MockHttpSession();

    @DisplayName("게시물 삭제 성공")
    @Test
    public void deletePost_success() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(url)
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

    @DisplayName("게시물 삭제 실패 - 유효하지 않은 게시물")
    @Test
    public void whenDeletePostNoExistPost_thenFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long invalidPostId = post.getId() + 1;
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + invalidPostId;
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(url)
                                .session(session)
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

    @DisplayName("게시물 삭제 실패 - 유효하지 않은 사용자")
    @Test
    public void whenDeletePostNoRightUser_thenFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();
        
        Member invalidMember = MemberTest.create("invalid", "invalid", UserRole.USER);
        em.persist(invalidMember);
        Long invalidMemberId = invalidMember.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, invalidMemberId);
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(url)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isForbidden()
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 삭제 실패 - 세션 없음")
    @Test
    public void whenDeletePostNoSession_thenFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(url)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isUnauthorized()
                )
                .andDo(MockMvcResultHandlers.print());
    }

    private Post persistNewPost() {
        Post post = PostTest.create("username", "nickname");
        em.persist(post.getWriter());
        em.persist(post);
        return post;
    }
}
