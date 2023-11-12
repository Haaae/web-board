package toy.board.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import toy.board.controller.comment.dto.request.CommentCreationRequest;
import toy.board.controller.comment.dto.request.CommentUpdateRequest;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;

// TODO: 2023-11-12 refactor 
@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CommentControllerTest {

    private static final String PREFIX = "/posts";
    public static final String POST_URL = PREFIX;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();
    private Long memberId;
    private Long postId;
    private Long commentId;
    private Long replyId;
    private String tooLongContent = "contentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentcontencontentcontentcontentcontentcontentcontentcontentcontentconten";

    @BeforeEach
    @Transactional
    void init() {
        Member member = MemberTest.create("username", "emankcin", UserRole.USER);
        em.persist(member);
        memberId = member.getId();
        em.flush();
        em.clear();
    }

    @AfterEach
    @Transactional
    void reset() {
        Member member = em.find(Member.class, memberId);
        em.remove(member);
        memberId = null;
    }

    @DisplayName("댓글 생성: 성공")
    @Test
    public void whenCreateCommentWithSession_thenUnauthorizedFail() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.COMMENT, null);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("대댓글 생성: 성공")
    @Test
    public void createComment_success() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long commentId = this.commentId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.REPLY, commentId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    // 세션 없음
    @DisplayName("Comment 생성 실패: 세션 없음")
    @Test
    public void whenCreateCommentWithNoSession_thenExceptionThrow() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long commentId = this.commentId;
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.REPLY, commentId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("Comment 생성 실패: comment 생성 시 commentId가 null이 아님")
    @Test
    public void PostControllerTest() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long commentId = this.commentId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.COMMENT, commentId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("Comment 생성 실패: postId가 유효하지 않음")
    @Test
    public void whenCreateCommentInvalidPostId_theFail() throws Exception {
        //given
        Long memberId = this.memberId;
        Long invalidPostId = -1L;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.COMMENT, null);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + invalidPostId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment 생성 실패: reply 생성 시 commentId가 유효하지 않음")
    @Test
    public void whenCreateCommentInvalidCommentId_theFail() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long invalidCommentId = -1L;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.COMMENT, invalidCommentId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment 생성 실패: reply 생성 시 commentId의 타입이 reply")
    @Test
    public void whenCreateCommentInvalidCommentType_theFail() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long replyId = this.replyId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.REPLY, replyId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment 생성 실패: reply 생성 시 commentId가 null임")
    @Test
    public void whenCreateCommentInvalidCommentIsNull_theFail() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", CommentType.REPLY, null);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment 생성 실패: empty content")
    @Test
    public void whenCreateCommentEmptyContent_theFail() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long commentId = this.commentId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest(null, CommentType.REPLY, commentId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment 생성 실패: content length 초과")
    @Test
    public void whenCreateCommentTooLongLength_theFail() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long commentId = this.commentId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest(tooLongContent, CommentType.REPLY, commentId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("댓글 생성 실패: null type")
    @Test
    public void whenCreateCommentTypeNull() throws Exception {
        //given
        Long memberId = this.memberId;
        Long postId = this.postId;
        Long commentId = this.commentId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        CommentCreationRequest request =
                new CommentCreationRequest("content", null, commentId);
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments";
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment update 성공")
    @Test
    public void commentUpdate_success() throws Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;
        //when
        CommentUpdateRequest request = new CommentUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment update 실패 - 존재하지 않는 comment id")
    @Test
    public void whenCommentUpdateWithNoExistCommentId_theFail() throws Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = -1L;
        //when
        CommentUpdateRequest request = new CommentUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment update 실패 - 세션 없음")
    @Test
    public void whenCommentUpdateWithNoSession_theFail() throws Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long commentId = this.commentId;
        //when
        CommentUpdateRequest request = new CommentUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment update 실패 - 권한 없는 사용자")
    @Test
    public void whenCommentUpdateWithNoRightUser_theFail() throws Exception {
        //given
        Long postId = this.postId;

        Member member = MemberTest.create("invalid", "invalid", UserRole.USER);
        em.persist(member);
        Long memberId = member.getId();

        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;

        //when
        CommentUpdateRequest request = new CommentUpdateRequest("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .content(content)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment update 실패 - 빈 본문")
    @Test
    public void whenUpdateCommentWithEmptyContent_theFail() throws Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;
        //when
        CommentUpdateRequest request = new CommentUpdateRequest("");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("comment update 실패 - 너무 긴 본문")
    @Test
    public void whenUpdateCommentWithTooLongContent_theFail() throws Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;
        //when
        CommentUpdateRequest request = new CommentUpdateRequest(
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
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("존재하지 않는 commentId")
    @Test
    public void whenDeleteNoExistCommentId_theFail() throws Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        Long commentId = -1L;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}