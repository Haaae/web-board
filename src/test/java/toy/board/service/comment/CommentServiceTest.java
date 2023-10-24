package toy.board.service.comment;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private CommentService commentService;

    private Long memberId;
    private Post post;
    private String nickname;
    private String content = "content";
    private String title = "title";

    @BeforeEach
    void init() {
        Member member = MemberTest.create("username", "emankcin");
        em.persist(member);
        memberId = member.getId();
        nickname = member.getProfile().getNickname();
        Post newPost = new Post(member, title, content);
        em.persist(newPost);
        post = newPost;
        em.flush(); em.clear();
    }

    //create
    @DisplayName("create 성공")
    @Test
    public void create_success() throws  Exception {
        //given
        Optional parentId = Optional.empty();
        Long postId = post.getId();
        Long memberId = this.memberId;
        //when
        Long commentId = commentService.create(content, CommentType.COMMENT, parentId, postId, memberId);
        Long replyId = commentService.create(content, CommentType.REPLY, Optional.of(commentId), postId, memberId);
        Comment comment = em.find(Comment.class, commentId);
        Comment reply = em.find(Comment.class, replyId);
        //then
        assertThat(comment).isNotNull();
        assertThat(reply).isNotNull();
        assertThat(comment).isEqualTo(reply.getParent());
        assertThat(comment.getReplies().contains(reply)).isTrue();
    }

    @DisplayName("create 실패: post id에 맞는 post가 존재하지 않음")
    @Test
    public void whenTryToCreateWithNotExistPostId_thenThrowException() throws  Exception {
        //given
        Optional parentId = Optional.empty();
        Long memberId = this.memberId;
        //when
        Long invalidPostId = 12132L;
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.create(content, CommentType.COMMENT, parentId, invalidPostId,
                        memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_FOUND);
    }

    @DisplayName("create 실패: memberId에 맞는 회원 닉네임이 존재하지 않음")
    @Test
    public void whenTryToCreateWithNotExistMemberId_thenThrowException() throws  Exception {
        //given
        Optional parentId = Optional.empty();
        Long postId = post.getId();
        //when
        Long invalidMemberId = 2243L;
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.create(content, CommentType.COMMENT, parentId, postId,
                        invalidMemberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.ACCOUNT_NOT_FOUND);
    }

    @DisplayName("create 실패: 대댓글 생성 시 주어진 CommentId에 해당하는 Comment가 존재하지 않음")
    @Test
    public void whenTryToCreateWithNotExistComment_thenThrowException() throws  Exception {
        //given
        Optional parentId = Optional.of(21342L);
        Long postId = post.getId();
        Long memberId = this.memberId;
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.create(content, CommentType.COMMENT, parentId, postId,
                        memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.COMMENT_NOT_FOUND);
    }

    @DisplayName("create 실패: 대댓글 생성 시 주어진 CommentId가 NULL임")
    @Test
    public void CommentServiceTest() throws  Exception {
        //given
        Optional parentId = Optional.empty();
        Long postId = post.getId();
        Long memberId = this.memberId;
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.create(content, CommentType.REPLY, parentId, postId,
                        memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.INVALID_COMMENT_TYPE);
    }

    // update
    @DisplayName("update 성공")
    @Test
    public void update_success() throws  Exception {
        //given
        Long commentId = createComment();
        String updateContent = "update comment";
        //when
        Long updatedCommentId = commentService.update(commentId, updateContent, memberId);
        Comment findComment = em.find(Comment.class, updatedCommentId);
        //then
        assertThat(findComment.getId()).isEqualTo(commentId);
        assertThat(findComment.getContent()).isEqualTo(updateContent);
    }

    @DisplayName("update 실패: member가 수정 권한 없음")
    @Test
    public void whenMemberHasNoRightForUpdate_thenThrowException() throws  Exception {
        //given
        Long commentId = createComment();
        String updateContent = "update comment";
        //when
        Member invalidMember = MemberTest.create("invalid", "invalid");
        em.persist(invalidMember);
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.update(commentId, updateContent, invalidMember.getId()));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.COMMENT_NOT_WRITER);
    }

    @DisplayName("update 실패: comment id에 해당하는 comment가 없음")
    @Test
    public void whenCommentNotExist_thenThrowException() throws  Exception {
        //given
        String updateContent = "update comment";
        //when
        Long invalidCommentId = 234L;
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.update(invalidCommentId, updateContent, memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.COMMENT_NOT_FOUND);
    }

    // remove
    @DisplayName("remove 성공")
    @Test
    public void delete_success() throws  Exception {
        //given
        Long commentId = createComment();
        Long memberId = this.memberId;
        //when
        commentService.delete(commentId, memberId);
        Comment findComment = em.find(Comment.class, commentId);
        //then
        assertThat(findComment.isDeleted()).isTrue();
    }

    @DisplayName("delete 실패: member가 삭제 권한 없음")
    @Test
    public void whenMemberHasNoRightForDelete_thenThrowException() throws  Exception {
        //given
        Long commentId = createComment();
        //when
        Member invalidMember = MemberTest.create("invalid", "invalid");
        em.persist(invalidMember);
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.delete(commentId, invalidMember.getId()));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.COMMENT_NOT_WRITER);
    }

    @DisplayName("delete 실패: comment id에 해당하는 comment가 없음")
    @Test
    public void whenCommentNotExistToDelete_thenThrowException() throws  Exception {
        //given
        //when
        Long invalidCommentId = 234L;
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> commentService.delete(invalidCommentId, memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.COMMENT_NOT_FOUND);
    }

    private Long createComment() {
        Optional parentId = Optional.empty();
        Long postId = post.getId();
        Long memberId = this.memberId;
        return commentService.create(content, CommentType.COMMENT, parentId, postId, memberId);
    }
}