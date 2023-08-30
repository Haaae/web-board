package toy.board.service;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private EntityManager em;

    @DisplayName("post create의 반환값이 정상적으로 반환됨")
    @Test
    public void PostServiceTest() throws  Exception {
        //given
        Member member = MemberTest.create();
        em.persist(member);
        em.flush(); em.clear();
        //when
        Long postId = postService.create("title", "content", member.getId());
        Post post = em.find(Post.class, postId);
        //then
        System.out.println("postId = " + postId);
        assertThat(postId).isNotNull();
        assertThat(postId).isEqualTo(post.getId());
    }


}