package toy.board.repository.comment;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Comment;

public interface CommentQueryRepository {

    Optional<Comment> findCommentWithFetchJoinWriter(final Long id);

    Page<Comment> findAllNotDeletedCommentByWriterIdWithFetchJoinPostAndWriter(final Long writerId,
                                                                               final Pageable pageable);

}
