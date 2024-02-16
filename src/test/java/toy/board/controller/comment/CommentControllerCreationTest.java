package toy.board.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import toy.board.controller.comment.dto.request.CommentCreationRequest;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CommentControllerCreationTest {

    public static final String POST_URL = "/posts";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();

    private String tooLongContent = "contentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentconten";


    @DisplayName("댓글 생성: 성공")
    @Test
    public void whenCreateCommentWithSession_thenUnauthorizedFail() throws Exception {
        //given
        Post post = persistNewPost();

        Long memberId = post.getWriterId();
        Long postId = post.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.COMMENT,
                null
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isCreated()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );

    }

    @DisplayName("대댓글 생성: 성공")
    @Test
    public void createComment_success() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.REPLY,
                commentId
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isCreated()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    // 세션 없음
    @DisplayName("Comment 생성 실패: 세션 없음")
    @Test
    public void whenCreateCommentWithNoSession_thenExceptionThrow() throws Exception {
        //given
        Post post = persistNewPost();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.REPLY,
                commentId
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(url)
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
                );
    }

    @DisplayName("Comment 생성 실패: comment 생성 시 commentId가 null이 아님")
    @Test
    public void PostControllerTest() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.COMMENT,
                commentId
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("Comment 생성 실패: postId가 유효하지 않음")
    @Test
    public void whenCreateCommentInvalidPostId_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();

        Long invalidPostId = -1L;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.COMMENT,
                null
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + invalidPostId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("comment 생성 실패: reply 생성 시 commentId가 유효하지 않음")
    @Test
    public void whenCreateCommentInvalidCommentId_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Long invalidCommentId = -1L;

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.COMMENT,
                invalidCommentId
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("comment 생성 실패: reply 생성 시 commentId의 타입이 reply")
    @Test
    public void whenCreateCommentInvalidCommentType_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);

        Comment reply = new Comment(post, post.getWriter(), "content", CommentType.REPLY, comment);
        em.persist(reply);
        Long replyId = reply.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.REPLY,
                replyId
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("comment 생성 실패: reply 생성 시 commentId가 null임")
    @Test
    public void whenCreateCommentInvalidCommentIsNull_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                CommentType.REPLY,
                null
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("comment 생성 실패: empty content")
    @Test
    public void whenCreateCommentEmptyContent_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                null,
                CommentType.REPLY,
                commentId
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("comment 생성 실패: content length 초과")
    @Test
    public void whenCreateCommentTooLongLength_theFail() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        Comment comment = persistNewComment(post);
        Long commentId = comment.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                tooLongContent,
                CommentType.REPLY,
                commentId
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("댓글 생성 실패: null type")
    @Test
    public void whenCreateCommentTypeNull() throws Exception {
        //given
        Post post = persistNewPost();
        Long memberId = post.getWriterId();
        Long postId = post.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request = new CommentCreationRequest(
                "content",
                null,
                null
        );
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
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
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
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
