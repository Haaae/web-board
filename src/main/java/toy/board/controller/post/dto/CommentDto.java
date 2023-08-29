package toy.board.controller.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommentDto(
        Long commentId,
        Long writerId,
        String writer,
        String content,
        boolean isDeleted,
        boolean isModified,
        LocalDateTime createdDate,
        List<CommentDto> replies
) {

}
