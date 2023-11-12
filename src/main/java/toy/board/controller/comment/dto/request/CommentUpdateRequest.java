package toy.board.controller.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Comment;

@Schema(description = "댓글 수정 요청 DTO")
public record CommentUpdateRequest(
        @Schema(description = "본문")
        @NotBlank
        @Size(max = Comment.CONTENT_LENGTH)
        String content
) {

}
