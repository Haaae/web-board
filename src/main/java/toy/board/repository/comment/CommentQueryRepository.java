package toy.board.repository.comment;

import toy.board.repository.comment.dto.CommentListDto;

public interface CommentQueryRepository {

    CommentListDto getCommentListDtoByPostId(Long postId);
}
