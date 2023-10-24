package toy.board.repository.comment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    /**
     * Comment 반환 시 Member, Profile을 fetch join한다.
     * ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고,
     * 연계하여 fetch join할 수 있다.
     * @param id must not be {@literal null}.
     * @return
     */
    @Query(value = "SELECT c FROM Comment c "
            + "LEFT JOIN FETCH c.writer AS w "
            + "LEFT JOIN FETCH w.profile "
            + "WHERE c.id = :commentId"
    )
    Optional<Comment> findCommentById(@Param("commentId") final Long id);

    void deleteCommentsByPost(final Post post);

    void deleteCommentsByPostAndType(final Post post, final CommentType type);

}
