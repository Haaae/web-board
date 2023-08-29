package toy.board.repository.comment;

import java.util.List;
import toy.board.controller.post.dto.CommentDto;

public interface CommentQueryRepository {

    List<CommentDto> findCommentsConvertedToDtoUsingPostId(Long postId);
}
