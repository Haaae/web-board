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
        Comment comment = create();
        Post post = comment.getPost();
        Member member = comment.getWriter();

        em.persist(member);
        em.persist(post);
        em.persist(comment);

        long memberId = member.getId();

        //when

        //then
        Assertions.assertThrows(BusinessException.class, () -> comment.validateRight(memberId));
    }

    @DisplayName("권한 검증 실패")
    @Test
    public void whenValidateWithInvalidMemberId_thenValidateFail() throws  Exception {
        //given
        Comment comment = create();
        Post post = comment.getPost();
        Member member = comment.getWriter();

        em.persist(member);
        em.persist(post);
        em.persist(comment);

        long invalidMemberId = member.getId() + 1;

        //when

        //then
        Assertions.assertThrows(BusinessException.class, () -> comment.validateRight(invalidMemberId));
    }

    @DisplayName("orphanRemoval test - comment의 필드멤버 reply에 orphanRemoval 설정하면 comment가 삭제될 때만 reply를 삭제하는 단방향 전파가 성립된다. 이는 cascade도 마찬가지이다")
    @Test
    public void post_and_comment_with_no_cascade() throws Exception {
        //given
        Comment comment = create();
        Post post = comment.getPost();
        Member member = comment.getWriter();

        em.persist(member);
        em.persist(post);
        em.persist(comment);

        Comment parentComment = em.find(Comment.class, comment.getId());
        Comment reply = new Comment(
                post,
                member,
                "content",
                CommentType.REPLY,
                parentComment
        );

        em.persist(reply);
        em.flush();
        em.clear();

        // when then
        Comment findReply = em.find(Comment.class, reply.getId());
        em.remove(findReply); // parent에 casecade 설정이 되어있지 않으므로 해당 엔티티의 삭제가 parent에게 전파되지 않는다.
        em.flush();
        em.clear();

        Comment findComment = em.find(Comment.class, comment.getId());
        assertThat(findComment).isNotNull();
    }

    private static Comment create() {
        Post post = PostTest.create();
        return new Comment(
                post,
                post.getWriter(),
                "content",
                CommentType.COMMENT,
                null
        );
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

}