package toy.board.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import toy.board.controller.post.dto.CommentCreationRequest;
import toy.board.controller.post.dto.CommentUpdateDto;
import toy.board.controller.post.dto.PostCreationRequest;
import toy.board.controller.post.dto.PostUpdateDto;
import toy.board.domain.auth.Login;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;

@Transactional
@ExtendWith(MockitoExtension.class) // Mockito와 같은 확장 기능을 테스트에 통합시켜주는 어노테이션
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc // controller뿐만 아니라 service와 repository 등의 컴포넌트도 mock으로 올린다.
class PostControllerTest {

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
        setupWithSavingPostAndComment();
        Member member = MemberTest.create();
        em.persist(member);
        memberId = member.getId();
        em.flush(); em.clear();
    }

    @AfterEach
    @Transactional
    void reset() {
        Member member = em.find(Member.class, memberId);
        em.remove(member);
        memberId = null;
    }

    @DisplayName("잘못된 페이징 정보로 목록을 조회시 400에러를 응답한다.")
    @ParameterizedTest
    @CsvSource({"1,0", "-1,1", "1,two", "two,1"})
    public void response400WhenRequestByInvalidPagingInfo(String page, String size)
            throws Exception {
        //when
        String url = POST_URL + "?page=" + page + "&size=" + size;
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 목록 조회 성공: 세션이 없어도 성공")
    @Test
    public void getPostList_success() throws  Exception {
        //given
        int page = 0;
        int size = 10;
        //when
        String url = POST_URL + "?page=" + page + "&size=" + size;
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 상세 데이터 조회: 세션 없이 성공")
    @Test
    public void getPostDetail_success() throws  Exception {
        //given
        postId = this.postId;
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 생성: 세션 없으면 실패")
    @Test
    public void whenCreatePostWithNoSession_thenUnauthorizedFail() throws  Exception {
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
    public void whenCreatePostWithTooLongContent_thenFail() throws  Exception {
        //given
        Long memberId = this.memberId;
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
    public void whenCreatePostWithTooLongTitle_thenFail() throws  Exception {
        //given
        Long memberId = this.memberId;
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
    public void whenCreatePostWithEmptyContent_thenFail() throws  Exception {
        //given
        Long memberId = this.memberId;
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
    public void whenCreatePostWithEmptyTitle_thenFail() throws  Exception {
        //given
        Long memberId = this.memberId;
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
    public void whenCreatePostWithSession_thenUnauthorizedFail() throws  Exception {
        //given
        Long memberId = this.memberId;
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
    public void whenCreateCommentWithNoSession_thenExceptionThrow() throws  Exception {
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
    public void PostControllerTest() throws  Exception {
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
    public void whenCreateCommentInvalidPostId_theFail() throws  Exception {
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
    public void whenCreateCommentInvalidCommentId_theFail() throws  Exception {
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
    public void whenCreateCommentInvalidCommentType_theFail() throws  Exception {
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
    public void whenCreateCommentInvalidCommentIsNull_theFail() throws  Exception {
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
    public void whenCreateCommentEmptyContent_theFail() throws  Exception {
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
    public void whenCreateCommentTooLongLength_theFail() throws  Exception {
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

    // update post 성공
    @DisplayName("update post 성공")
    @Test
    public void updatePost_success() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long writerId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, writerId);
        //when
        PostUpdateDto request = new PostUpdateDto("content");
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
    public void whenUpdatePostWithNoSession_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        //when
        PostUpdateDto request = new PostUpdateDto("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("post update - 작성자와 다른 회원")
    @Test
    public void whenUpdatePostNoRightMember_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId() + 1;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostUpdateDto request = new PostUpdateDto("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("post update - 존재하지 않는 post id")
    @Test
    public void whenUpdatePostNotExistPostId_theFail() throws  Exception {
        //given
        Long invalidPostId = -1L;
        Long memberId = this.memberId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostUpdateDto request = new PostUpdateDto("content");
        String content = mapper.writeValueAsString(request);
        String url = POST_URL + "/" + invalidPostId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("post update - 너무 긴 본문")
    @Test
    public void whenUpdatePostTooLongContent_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostUpdateDto request = new PostUpdateDto(
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
    public void whenUpdatePostEmptyContent_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        PostUpdateDto request = new PostUpdateDto(null);
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

    @DisplayName("comment update 성공")
    @Test
    public void commentUpdate_success() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;
        //when
        CommentUpdateDto request = new CommentUpdateDto("content");
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
    public void whenCommentUpdateWithNoExistCommentId_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = -1L;
        //when
        CommentUpdateDto request = new CommentUpdateDto("content");
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
    public void whenCommentUpdateWithNoSession_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long commentId = this.commentId;
        //when
        CommentUpdateDto request = new CommentUpdateDto("content");
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
    public void whenCommentUpdateWithNoRightUser_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Long memberId = -1L;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;
        //when
        CommentUpdateDto request = new CommentUpdateDto("content");
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
    public void whenUpdateCommentWithEmptyContent_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;
        //when
        CommentUpdateDto request = new CommentUpdateDto("");
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
    public void whenUpdateCommentWithTooLongContent_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        Long commentId = this.commentId;
        //when
        CommentUpdateDto request = new CommentUpdateDto(
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

    @DisplayName("게시물 삭제 성공")
    @Test
    public void deletePost_success() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 삭제 실패 - 유효하지 않은 게시물")
    @Test
    public void whenDeletePostNoExistPost_thenFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        postId = -1L;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 삭제 실패 - 유효하지 않은 사용자")
    @Test
    public void whenDeletePostNoRightUser_thenFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId() + 1;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 삭제 실패 - 세션 없음")
    @Test
    public void whenDeletePostNoSession_thenFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        //when
        String url = POST_URL + "/" + postId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시물 삭제 성공")
    @Test
    public void deleteComment_success() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId();
        Long commentId = this.commentId;
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

    @DisplayName("존재하지 않는 commentId")
    @Test
    public void whenDeleteNoExistCommentId_theFail() throws  Exception {
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

    @DisplayName("세션 없음")
    @Test
    public void whenDeleteNoSession_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long commentId = this.commentId;
        //when
        String url = POST_URL + "/" + postId + "/comments/" + commentId;
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("권한 없는 사용자")
    @Test
    public void whenDeleteNoRight_theFail() throws  Exception {
        //given
        Long postId = this.postId;
        Post post = em.find(Post.class, postId);
        Long memberId = post.getWriterId() + 1;
        Long commentId = this.commentId;
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);
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

    private void setupWithSavingPostAndComment() {
        Member member = Member.builder(
                "member",
                new Login("password"),
                Profile.builder("nickname").build(),
                LoginType.LOCAL_LOGIN,
                UserRole.USER
        ).build();
        em.persist(member);

        Member member2 = Member.builder(
                "membe2",
                new Login("password"),
                Profile.builder("nicknam2").build(),
                LoginType.LOCAL_LOGIN,
                UserRole.USER
        ).build();
        em.persist(member2);

        for (int i = 0; i < 30; i++) {
            Post post = new Post(member,
                    "title" + i,
                    "content" + i
            );

            em.persist(post);

            if (i == 0) {
                this.postId = post.getId();
            }

            for (int k = 0; k < 3; k++) {
                Comment comment = new Comment(
                        post,
                        member,
                        "comment" + String.valueOf(k),
                        CommentType.COMMENT,
                        null
                );
                em.persist(comment);

                if (i == 0 && k == 0) {
                    this.commentId = comment.getId();
                }

                for (int j = 0; j < 5; j++) {
                    Comment reply = new Comment(
                            post,
                            member2,
                            "reply" + String.valueOf(j),
                            CommentType.REPLY,
                            comment
                    );
                    em.persist(reply);
                    if (i == 0 && k == 0 && j == 0) {
                        this.replyId = reply.getId();
                    }
                }
            }
        }
        em.flush();
        em.clear();
    }
}