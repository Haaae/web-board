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

    // 게시물을 삭제하면 모든 댓글이 삭제됨 cascade - 양방향 필요 x 그냥 post id 로 comment fetch join 하면 될 듯?
    // 댓글을 삭제해도 게시물은 삭제되지 않음 - 양방향 필요 x
    // 유저를 삭제해도 게시물은 삭제되지 않음 - 양방향 필요 x

    @DisplayName("cascade test - 댓글에 cascade 설정하면 게시물을 삭제해도 삭제가 되나? => 무조건 양방향으로 연동된다.")
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
        Comment comment = new Comment(
                post,
                member.getId(),
                member.getProfile().getNickname(),
                "content",
                CommentType.COMMENT,
                null
        );

        em.persist(member);
        em.persist(post);
        em.persist(comment);
        em.flush();
        em.clear();

        // when then
        Comment findComment = em.find(Comment.class, comment.getId());
        Post findPost = em.find(Post.class, post.getId());
        Member findMember = em.find(Member.class, member.getId());
        assertThat(findPost).isNotNull();
        assertThat(findMember).isNotNull();

        em.remove(findComment); // 이때 post에 cascade를 걸지 않았으므로 post는 삭제되지 않는다
        em.flush();
        em.clear();

        Post findSecondPost = em.find(Post.class, post.getId());

        assertThat(findSecondPost).isNotNull();
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

    // 댓글만 불러오면 댓글에 대한 게시물이 불러오지 않는다. - 단방향일 때 확인 필요

    // 댓글을 삭제하면 모든 대댓글이 삭제됨 -> 이건 서비스 구현 후 서비스에서 테스트

}