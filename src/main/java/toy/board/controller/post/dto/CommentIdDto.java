package toy.board.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "댓글 생성, 수정, 삭제 DTO")
public record CommentIdDto(
        @Schema(description = "생성한 댓글 Id")
        @Positive
        Long commentId
) {
    public static CommentIdDto of(Long commentId) {
        return new CommentIdDto(commentId);
    }
}
