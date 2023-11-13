package toy.board.controller.comment;

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
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CommentControllerDeleteTest {

    public static final String POST_URL = "/posts";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    private final MockHttpSession session = new MockHttpSession();

    @DisplayName("댓글 삭제 성공")
    @Test
    public void deleteComment_success() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("댓글 삭제 실패: 존재하지 않는 commentId")
    @Test
    public void whenDeleteNoExistCommentId_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long invalidCommentId = comment.getId() + 1;

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + postId + "/comments/" + invalidCommentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("댓글 삭제 실패: 세션 없음")
    @Test
    public void whenDeleteNoSession_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();
        //when
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("댓글 삭제 실패: 권한 없는 사용자")
    @Test
    public void whenDeleteNoRight_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();

        Member invalidMember = MemberTest.create("invalid", "invalid", UserRole.USER);
        em.persist(invalidMember);
        Long invalidMemberId = invalidMember.getId();
        
        session.setAttribute(SessionConst.LOGIN_MEMBER, invalidMemberId);
        //when
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }

    private Post persistNewPost() {
        Post post = PostTest.create("username", "nickname");
        em.persist(post.getWriter());
        em.persist(post);
        return post;
    }

    private Comment persistNewComment(Post post) {
        Comment comment = new Comment(post, post.getWriter(), "content", CommentType.COMMENT, null);
        em.persist(comment);
        return comment;
    }
}
