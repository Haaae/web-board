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
import toy.board.controller.post.dto.request.PostUpdateRequest;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PostUpdateControllerTest {

    public static final String POST_URL = "/posts";

    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    private String tooLongContent = "contentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentconten";


    @DisplayName("update post 성공")
    @Test
    public void updatePost_success() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();
        Long writerId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, writerId);
        //when
        PostUpdateRequest request = new PostUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("post update - 세션 없음")
    @Test
    public void whenUpdatePostWithNoSession_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();
        //when
        PostUpdateRequest request = new PostUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers.status()
                                .isUnauthorized()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("post update - 작성자와 다른 회원")
    @Test
    public void whenUpdatePostNoRightMember_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();

        Member invalidMember = MemberTest.create("invalid", "invalid", UserRole.USER);
        em.persist(invalidMember);
        Long invalidMemberId = invalidMember.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, invalidMemberId);
        //when
        PostUpdateRequest request = new PostUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers.status()
                                .isForbidden()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("post update - 존재하지 않는 post id")
    @Test
    public void whenUpdatePostNotExistPostId_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long invalidPostId = post.getId() + 1;
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostUpdateRequest request = new PostUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + invalidPostId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(
                        MockMvcResultMatchers.status()
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("post update - 너무 긴 본문")
    @Test
    public void whenUpdatePostTooLongContent_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostUpdateRequest request = new PostUpdateRequest(
                tooLongContent +
                        tooLongContent +
                        tooLongContent +
                        tooLongContent +
                        tooLongContent +
                        tooLongContent +
                        tooLongContent +
                        tooLongContent +
                        tooLongContent +
                        tooLongContent
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("post update - 빈 본문")
    @Test
    public void whenUpdatePostEmptyContent_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostUpdateRequest request = new PostUpdateRequest(null);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private Post persistNewPost() {
        Post post = PostTest.create("username", "nickname");
        em.persist(post.getWriter());
        em.persist(post);
        return post;
    }
}
