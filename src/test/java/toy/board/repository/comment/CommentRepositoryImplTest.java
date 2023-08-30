package toy.board.repository.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.controller.post.dto.CommentDto;
import toy.board.domain.auth.Login;
import toy.board.domain.post.Post;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;

@SpringBootTest
class CommentRepositoryImplTest {

    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    CommentRepository commentRepository;

    @Transactional
    @DisplayName("post의 comment가 없을 때 comment dto list의 반환값 리스트는 size가 0이다")
    @Test
    public void whenPostHasNoComment_thenCommentDtoReturnWhat() throws  Exception {
        //given
        Member member = Member.builder(
                "member",
                new Login("password"),
                Profile.builder("nickname").build(),
                LoginType.LOCAL_LOGIN,
                UserRole.USER
        ).build();
        em.persist(member);

        Post post = new Post(
                member.getId(),
                member.getProfile().getNickname(),
                "title",
                "content"
        );
        em.persist(post);
        em.flush();
        em.clear();

        //when
        List<CommentDto> comments = commentRepository.getCommentDtosByPostId(post.getId());

        //then
        assertThat(comments.size()).isEqualTo(0);
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> comments.get(0));
    }

}