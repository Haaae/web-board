package toy.board.controller.comment.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "댓글 생성, 수정, 삭제 요청 DTO")
public record CommentIdResponse(
        @Schema(description = "생성한 댓글 Id")
        @Positive
        Long commentId
) {
    public static CommentIdResponse of(Long commentId) {
        return new CommentIdResponse(commentId);
    }
}
