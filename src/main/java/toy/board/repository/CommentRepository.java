package toy.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.post.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // TODO: 2023-08-25 페이징 복습 후 적용
}
