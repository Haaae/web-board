package toy.board.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.post.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

}
