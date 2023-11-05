package toy.board.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Post;

import java.util.Optional;

public interface PostQueryRepository {

    Optional<Post> findPostWithFetchJoinWriterAndProfileAndComments(Long postId);

    Optional<Post> findPostWithFetchJoinWriterAndProfile(Long postId);

    Page<Post> findAllWithFetchJoinWriterAndProfile(final Pageable pageable);

    Page<Post> findAllByWriterIdFetchJoinWriterAndProfile(final Long writerId, final Pageable pageable);

}
