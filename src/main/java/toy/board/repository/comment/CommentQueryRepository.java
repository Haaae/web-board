package toy.board.repository.comment;

import java.util.List;
import toy.board.repository.comment.dto.CommentDto;

public interface CommentQueryRepository {

    List<CommentDto> getCommentDtosByPostId(Long postId);
}
