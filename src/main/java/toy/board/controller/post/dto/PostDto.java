package toy.board.controller.post.dto;

import java.time.LocalDateTime;

public record PostDto(
        Long postId,
        Long writerId,
        String writer,
        String title,
        String content,
        Long hits,
        LocalDateTime createdDate,
        boolean isModified,
        Long commentNum
) {
}
