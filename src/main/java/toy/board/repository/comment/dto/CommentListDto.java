package toy.board.repository.comment.dto;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record CommentListDto(
        List<CommentDto> commentDtos
) {
    public int countTotalCommentNum() {
        long countCommentType = countCommentType();
        AtomicInteger countTotal = plusReplyTypeCount(countCommentType);

        return countTotal.get();
    }

    private AtomicInteger plusReplyTypeCount(long countCommentType) {
        AtomicInteger countTotal = new AtomicInteger((int) countCommentType);
        commentDtos.stream()
                .filter(CommentDto::isCommentType)
                .forEach(c ->
                        countTotal.addAndGet(
                                c.replies()
                                        .countTotalCommentNum()
                        )
                );
        return countTotal;
    }

    private long countCommentType() {
        return commentDtos.stream()
                .filter(CommentDto::isCommentType)
                .count();
    }
}
