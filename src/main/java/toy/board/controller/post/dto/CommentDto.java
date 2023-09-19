package toy.board.controller.post.dto;

import toy.board.domain.post.CommentType;

import java.time.LocalDateTime;
import java.util.List;

public record CommentDto(
        Long commentId,
        Long writerId,
        String writer,
        String content,
        CommentType type,
        boolean isDeleted,
        boolean isModified,
        LocalDateTime createdDate,
        List<CommentDto> replies
) {

}
