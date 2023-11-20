package toy.board.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.post = :post")
    void deleteCommentsByPost(@Param("post") final Post post);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.post = :post and c.type = :type")
    void deleteCommentsByPostAndType(@Param("post") final Post post, @Param("type") final CommentType type);

}
