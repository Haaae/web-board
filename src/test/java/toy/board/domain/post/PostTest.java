package toy.board.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.MemberTest;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@SpringBootTest
@Transactional
public class PostTest {

    @Autowired
    private EntityManager em;

    @DisplayName("권한 없는 사용자가 게시물을 수정하려 할 경우 예외 발생")
    @Test
    public void whenUpdatePostWithNotValidWriterId_thenThrowException() throws Exception {
        //given
        Post post = create();
        em.persist(post.getWriter());
        em.persist(post);
        String newContent = "new";
        //when
        Long invalidWriterId = post.getWriterId() + 1;
        //then
        assertThrows(BusinessException.class,
                () -> post.update(newContent, invalidWriterId),
                ExceptionCode.POST_NOT_WRITER.getDescription()
        );
    }

    @DisplayName("사용자에 의한 게시물 수정 성공")
    @Test
    public void whenUpdatePostWithValidWriterId_thenSuccess() throws Exception {
        //given
        Post post = create();
        em.persist(post.getWriter());
        em.persist(post);
        String newContent = "new";
        Long writerId = post.getWriterId();
        //when
        post.update(newContent, writerId);
        //then
        assertThat(post.getContent()).isEqualTo(newContent);
    }

    public static Post create() {
        return new Post(MemberTest.create(), "title", "content");
    }

}