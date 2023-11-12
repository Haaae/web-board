package toy.board.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Comment;

@Schema(description = "댓글 수정 요청 DTO")
public record CommentUpdateDto(
        @Schema(description = "본문")
        @NotBlank
        @Size(max = Comment.CONTENT_LENGTH)
        String content
) {

}
