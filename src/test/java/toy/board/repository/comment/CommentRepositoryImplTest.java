package toy.board.repository.comment;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.domain.user.UserRole;

@DataJpaTest
@Transactional
class CommentRepositoryImplTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("사용자가 작성한 Comment 중 삭제 상태인 Comment는 가져오지 않음")
    @Test
    public void 댓글_가져오기_삭제된_댓글_제외() throws Exception {
        //given
        Member member = persistMember();
        Post post = persistPost(member);

        Comment comment = new Comment(post, member, "title", CommentType.COMMENT, null);
        em.persist(comment);

        Comment reply1 = new Comment(post, member, "title", CommentType.REPLY, comment);
        Comment reply2 = new Comment(post, member, "title", CommentType.REPLY, comment);
        Comment reply3 = new Comment(post, member, "title", CommentType.REPLY, comment);
        em.persist(reply1);
        em.persist(reply2);
        em.persist(reply3);

        comment.deleteBy(member);

        em.flush();
        em.clear();

        int size = 10;
        int pageNum = 0;
        int totalPages = 1;
        int totalElementsNotDeleted = 3;
        String sort = "createdDate";

        PageRequest pageable = PageRequest.of(pageNum, size, Sort.by(sort));

        //when
        Page<Comment> page = commentRepository
                .findAllNotDeletedCommentByWriterIdWithFetchJoinPostAndWriter(comment.getWriterId(), pageable);

        //then
        assertThat(page.getSize()).isEqualTo(size);
        assertThat(page.getTotalElements()).isEqualTo(totalElementsNotDeleted);
        assertThat(page.getTotalPages()).isEqualTo(totalPages);

        for (Comment findComment : page.getContent()) {
            assertThat(findComment.isCommentType()).isFalse();
        }
    }

    private Post persistPost(Member member) {
        Post post = new Post(
                member,
                "title",
                "content"
        );
        em.persist(post);
        return post;
    }

    private Member persistMember() {
        Member member = new Member(
                "member",
                "nickname",
                "password",
                UserRole.USER
        );
        em.persist(member);
        return member;
    }
}