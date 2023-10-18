package toy.board.domain.post;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;

@SpringBootTest
@Transactional
class CommentTest {

    @Autowired
    private EntityManager em;

    @DisplayName("orphanRemoval test - comment의 필드멤버 reply에 orphanRemoval 설정하면 comment가 삭제될 때만 reply를 삭제하는 단방향 전파가 성립된다. 이는 cascade도 마찬가지이다")
    @Test
    public void post_and_comment_with_no_cascade() throws Exception {
        //given
        Member member = MemberTest.create();
        Post post = new Post(
                member.getId(),
                member.getProfile().getNickname(),
                "title",
                "content"
        );
        em.persist(member);

        Comment comment = new Comment(
                post,
                member.getId(),
                member.getProfile().getNickname(),
                "content",
                CommentType.COMMENT,
                null
        );

        em.persist(post);
        em.persist(comment);

        Comment parentComment = em.find(Comment.class, comment.getId());
        Comment reply = new Comment(
                post,
                member.getId(),
                member.getProfile().getNickname(),
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


    @DisplayName("타입에 따른 생성자 구분으로 자동 양방향 매핑")
    @Test
    public void CommentTypeTest() throws  Exception {
        //given
        Post post = PostTest.create();
        String content = "content";
        long memberId = 1L;
        //when
        CommentType commentType = CommentType.COMMENT;
        CommentType replyType = CommentType.REPLY;

        //then
        Comment parent = new Comment(post, memberId, post.getWriter(), content, commentType, null);
        Comment reply = new Comment(post, memberId, post.getWriter(), content, replyType, parent);

        System.out.println("parent = " + parent);
        System.out.println("reply = " + reply);
        assertThat(parent.getReplies().contains(reply)).isTrue();
        assertThat(reply.getParent().equals(parent)).isTrue();
    }

}