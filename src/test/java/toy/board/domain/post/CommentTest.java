package toy.board.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@SpringBootTest
@Transactional
class CommentTest {

    @Autowired
    private EntityManager em;

    @DisplayName("권한 검증 성공")
    @Test
    public void whenValidateWithMemberId_thenValidateSuccess() throws  Exception {
        //given
        Comment comment = create(PostTest.create("username", "emankcin"), CommentType.COMMENT);
        Post post = comment.getPost();
        Member member = comment.getWriter();

        em.persist(member);
        em.persist(post);
        em.persist(comment);
        em.flush();em.clear();

        Long commentId = comment.getId();
        long memberId = member.getId();

        //when
        Comment findComment = em.find(Comment.class, commentId);
        Member findMember = em.find(Member.class, memberId);
        //then
        Assertions.assertDoesNotThrow(() -> findComment.validateRight(findMember));
    }

    @DisplayName("권한 검증 실패")
    @Test
    public void whenValidateWithInvalidMemberId_thenValidateFail() throws  Exception {
        //given
        Comment comment = create(PostTest.create("username", "emankcin"), CommentType.COMMENT);
        Post post = comment.getPost();
        Member member = comment.getWriter();

        em.persist(member);
        em.persist(post);
        em.persist(comment);

        Member invalidMember = MemberTest.create("invalid", "invalid");

        //when

        //then
        Assertions.assertThrows(BusinessException.class, () -> comment.validateRight(invalidMember));
    }

    @DisplayName("게시물이 동일할 때 타입에 따른 생성자 구분으로 자동 양방향 매핑")
    @Test
    public void CommentTypeTest() throws  Exception {
        //given
        Post post = PostTest.create("username", "emankcin");
        em.persist(post.getWriter());
        em.persist(post);
        String content = "content";

        //when
        CommentType commentType = CommentType.COMMENT;
        CommentType replyType = CommentType.REPLY;

        //then
        Comment parent = new Comment(post, post.getWriter(), content, commentType, null);
        Comment reply = new Comment(post, post.getWriter(), content, replyType, parent);

        System.out.println("parent = " + parent);
        System.out.println("reply = " + reply);
        assertThat(parent.getReplies().contains(reply)).isTrue();
        assertThat(reply.getParent().equals(parent)).isTrue();
    }

    @DisplayName("유효하지 않은 타입에 따른 댓글 생성 실패")
    @Test
    public void whenInvalidType_thenFailToCreateComment() throws  Exception {
        //given
        Post post = PostTest.create("username", "emankcin");
        em.persist(post.getWriter());
        em.persist(post);
        String content = "content";

        CommentType commentType = CommentType.COMMENT;
        CommentType replyType = CommentType.REPLY;
        Comment parent = new Comment(post, post.getWriter(), content, commentType, null);
        Comment reply = new Comment(post, post.getWriter(), content, replyType, parent);

        //when
        //then

        // 댓글 생성 시 부모가 null이 아닐 경우
        BusinessException e1 = assertThrows(BusinessException.class,
                () -> new Comment(post, post.getWriter(), content, commentType, reply));
        assertThat(e1.getCode()).isEqualTo(ExceptionCode.INVALID_COMMENT_TYPE);

        // 답글 생성 시 부모가 null일 경우
        BusinessException e2 = assertThrows(BusinessException.class,
                () -> new Comment(post, post.getWriter(), content, replyType, null));
        assertThat(e2.getCode()).isEqualTo(ExceptionCode.INVALID_COMMENT_TYPE);

        // 답글 생성 시 부모가 답글일 경우
        BusinessException e3 = assertThrows(BusinessException.class,
                () -> new Comment(post, post.getWriter(), content, replyType, reply));
        assertThat(e3.getCode()).isEqualTo(ExceptionCode.INVALID_COMMENT_TYPE);

    }

    @DisplayName("답글 생성 시 답글과 댓글의 게시물이 다를 경우 생성 실패")
    @Test
    public void whenInvalidPostOfParent_thenFailToCreateReply() throws  Exception {
        //given
        Post post = PostTest.create("username", "emankcin");
        em.persist(post.getWriter());
        em.persist(post);
        String content = "content";

        CommentType commentType = CommentType.COMMENT;
        CommentType replyType = CommentType.REPLY;
        Comment parent = new Comment(post, post.getWriter(), content, commentType, null);

        //when
        Post otherPost = PostTest.create("invalid", "invalid");
        em.persist(otherPost.getWriter());
        em.persist(otherPost);

        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> new Comment(otherPost, otherPost.getWriter(), content, replyType, parent));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.INVALID_POST_OF_PARENT_COMMENT);
    }

    public static Comment create(Post post, CommentType commentType) {
        return new Comment(
                post,
                post.getWriter(),
                "content",
                commentType,
                null
        );
    }
}