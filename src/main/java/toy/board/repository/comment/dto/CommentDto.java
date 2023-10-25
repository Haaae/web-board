package toy.board.repository.comment.dto;

import toy.board.domain.post.CommentType;

import java.time.LocalDateTime;

public record CommentDto(
        Long commentId,
        Long writerId,
        String writer,
        String content,
        CommentType type,
        boolean isDeleted,
        boolean isModified,
        LocalDateTime createdDate,
        CommentListDto replies
) {
}
