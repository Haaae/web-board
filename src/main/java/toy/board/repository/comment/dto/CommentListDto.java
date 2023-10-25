package toy.board.repository.comment.dto;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public record CommentListDto(
        List<CommentDto> commentDtos
) {
    public long getTotalCommentNum() {
        AtomicLong commentNum = new AtomicLong(commentDtos.size());

        commentDtos.forEach(c ->
            commentNum.addAndGet(c.replies().size())
        );

        return commentNum.get();
    }
}
