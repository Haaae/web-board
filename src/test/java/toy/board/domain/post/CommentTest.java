package toy.board.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

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

@SpringBootTest
@Transactional
class CommentTest {

    @Autowired
    private EntityManager em;

    @DisplayName("권한 검증 성공")
    @Test
    public void whenValidateWithMemberId_thenValidateSuccess() throws  Exception {
        //given
        Comment comment = create(PostTest.create(), CommentType.COMMENT);
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
        Comment comment = create(PostTest.create(), CommentType.COMMENT);
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

    @DisplayName("타입에 따른 생성자 구분으로 자동 양방향 매핑")
    @Test
    public void CommentTypeTest() throws  Exception {
        //given
        Post post = PostTest.create();
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