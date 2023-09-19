package toy.board.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.post.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

}
