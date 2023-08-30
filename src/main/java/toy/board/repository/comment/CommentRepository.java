package toy.board.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    void deleteCommentsByPost(Post post);
}
