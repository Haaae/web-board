package toy.board.repository.post;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Post;

public interface PostQueryRepository {

    Optional<Post> findPostWithFetchJoinWriterAndComments(final Long postId);

    Optional<Post> findPostWithFetchJoinWriter(final Long postId);

    Page<Post> findAllWithFetchJoinWriter(final Pageable pageable);

    Page<Post> findAllByWriterIdWithFetchWriter(final Long writerId, final Pageable pageable);

}
