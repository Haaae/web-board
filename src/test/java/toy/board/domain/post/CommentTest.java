package toy.board.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@SpringBootTest
@Transactional
class CommentTest {

    @Autowired
    private EntityManager em;

    @DisplayName("권한 검증: 작성자가 탈퇴한 댓글")
    @Test
    public void 권한_테스트_작성자_탈퇴() throws Exception {
        //given
        Comment comment = create(
                PostTest.create("username", "emankcin"),
                CommentType.COMMENT
        );
        Member member = comment.getWriter();

        Member invalidMember = MemberTest.create(
                "invalid",
                "invalid",
                UserRole.USER
        );

        Member master = MemberTest.create(
                "Master",
                "master",
                UserRole.MASTER
        );
        Member admin = MemberTest.create(
                "Master",
                "master",
                UserRole.ADMIN
        );
        //when
        comment.applyWriterWithdrawal();

        //then
        BusinessException writerException = assertThrows(
                BusinessException.class,
                () -> comment.validateRight(member)
        );
        assertThat(writerException.getCode())
                .isEqualTo(ExceptionCode.INVALID_AUTHORITY);

        BusinessException invalidMemberException = assertThrows(
                BusinessException.class,
                () -> comment.validateRight(invalidMember)
        );
        assertThat(invalidMemberException.getCode())
                .isEqualTo(ExceptionCode.INVALID_AUTHORITY);

        assertDoesNotThrow(() -> comment.validateRight(master));
        assertDoesNotThrow(() -> comment.validateRight(admin));
    }

    @DisplayName("권한 검증: 권한 있는 다른 사용자")
    @Test
    public void 권한_테스트_권한_있는_다른_사용자() throws Exception {
        //given
        Comment comment = create(
                PostTest.create("username", "emankcin"),
                CommentType.COMMENT
        );

        //when
        Member master = MemberTest.create(
                "Master",
                "master",
                UserRole.MASTER
        );
        Member admin = MemberTest.create(
                "Master",
                "master",
                UserRole.ADMIN
        );
        //then
        assertDoesNotThrow(() -> comment.validateRight(master));
        assertDoesNotThrow(() -> comment.validateRight(admin));
    }

    @DisplayName("권한 검증: 작성자 성공")
    @Test
    public void whenValidateWithMemberId_thenValidateSuccess() throws Exception {
        //given
        Comment comment = create(
                PostTest.create("username", "emankcin"),
                CommentType.COMMENT
        );
        //when
        Member member = comment.getWriter();
        //then
        assertDoesNotThrow(() -> comment.validateRight(member));
    }

    @DisplayName("권한 검증: 권한 없는 사용자 실패")
    @Test
    public void whenValidateWithInvalidMemberId_thenValidateFail() throws Exception {
        //given
        Comment comment = create(
                PostTest.create("username", "emankcin"),
                CommentType.COMMENT
        );
        //when
        Member invalidMember = MemberTest.create(
                "invalid",
                "invalid",
                UserRole.USER
        );
        //then
        BusinessException e = assertThrows(
                BusinessException.class,
                () -> comment.validateRight(invalidMember)
        );
        assertThat(e.getCode())
                .isEqualTo(ExceptionCode.INVALID_AUTHORITY);
    }

    @DisplayName("게시물이 동일할 때 타입에 따른 생성자 구분으로 자동 양방향 매핑")
    @Test
    public void CommentTypeTest() throws Exception {
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
    public void whenInvalidType_thenFailToCreateComment() throws Exception {
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
        assertThat(e1.getCode()).isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);

        // 답글 생성 시 부모가 null일 경우
        BusinessException e2 = assertThrows(BusinessException.class,
                () -> new Comment(post, post.getWriter(), content, replyType, null));
        assertThat(e2.getCode()).isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);

        // 답글 생성 시 부모가 답글일 경우
        BusinessException e3 = assertThrows(BusinessException.class,
                () -> new Comment(post, post.getWriter(), content, replyType, reply));
        assertThat(e3.getCode()).isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);

    }

    @DisplayName("답글 생성 시 답글과 댓글의 게시물이 다를 경우 생성 실패")
    @Test
    public void whenInvalidPostOfParent_thenFailToCreateReply() throws Exception {
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
        assertThat(e.getCode()).isEqualTo(ExceptionCode.BAD_REQUEST_POST_OF_COMMENT);
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