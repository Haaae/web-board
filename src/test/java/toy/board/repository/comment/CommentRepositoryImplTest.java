package toy.board.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.auth.Login;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;
import toy.board.repository.comment.dto.CommentListDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentRepositoryImplTest {

    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    CommentRepository commentRepository;

    @Transactional
    @DisplayName("getCommentListDtoByPostId fetch join 결과 확인: 하나의 쿼리만 발생해야 함")
    @Test
    public void whenGetCommentListDtoByPostId_thenJustOneQuery() throws Exception {
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
                member,
                "title",
                "content"
        );
        em.persist(post);
        Long postId = post.getId();

        Comment comment = new Comment(post, member, "title", CommentType.COMMENT, null);
        em.persist(comment);
        Comment reply1 = new Comment(post, member, "title", CommentType.REPLY, comment);
        Comment reply2 = new Comment(post, member, "title", CommentType.REPLY, comment);
        Comment reply3 = new Comment(post, member, "title", CommentType.REPLY, comment);
        em.persist(reply1);
        em.persist(reply2);
        em.persist(reply3);

        em.flush();
        em.clear();

        //when
        commentRepository.getCommentListDtoByPostId(postId);

        //then
    }

    @Transactional
    @DisplayName("post의 comment가 없을 때 comment dto list의 반환값 리스트는 size가 0이다")
    @Test
    public void whenPostHasNoComment_thenCommentDtoReturnWhat() throws Exception {
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
                member,
                "title",
                "content"
        );
        em.persist(post);
        em.flush();
        em.clear();

        //when
        CommentListDto commentListDto = commentRepository.getCommentListDtoByPostId(post.getId());

        //then
        assertThat(commentListDto.commentDtos().size()).isEqualTo(0);
    }

}