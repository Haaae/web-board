package toy.board.config;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.constant.SessionConst;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;

@SpringBootTest
@Transactional
class AuditorAwareProviderTest {

    @Autowired
    EntityManager em;
    @Autowired
    HttpSession session;

    @DisplayName("createdBy lastModifiedBy 동작 확인")
    @Test
    public void AuditorAwareProviderTest() throws Exception {
        //given
        Post post = PostTest.create("username", "nickname");
        Member member = post.getWriter();

        em.persist(member);
        Long memberId = member.getId();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);

        em.persist(post);
        Long postId = post.getId();

        em.flush();
        em.clear();

        //when
        Post findPost = em.find(Post.class, postId);

        //then
        Assertions.assertThat(findPost.getCreatedBy())
                .isEqualTo(memberId);
    }
}