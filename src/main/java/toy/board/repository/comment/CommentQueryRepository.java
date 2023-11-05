package toy.board.repository.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Comment;

import java.util.Optional;

public interface CommentQueryRepository {

    Optional<Comment> findCommentWithFetchJoinWriterAndProfile(final Long id);

    Page<Comment> findAllNotDeletedCommentByWriterIdWithFetchJoinPostAndWriterAndProfile(final Long writerId, final Pageable pageable);

}
