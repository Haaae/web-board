package toy.board.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    void deleteCommentsByPost(final Post post);

    void deleteCommentsByPostAndType(final Post post, final CommentType type);

}
