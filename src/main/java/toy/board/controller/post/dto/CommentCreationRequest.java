package toy.board.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;

@Schema(description = "댓글 생성 요청 DTO")
public record CommentCreationRequest(
        @Schema(description = "본문")
        @NotBlank
        @Size(max = Comment.CONTENT_LENGTH)
        String content,
        @Schema(description = "댓글 타입: 댓글, 답글", example = "REPLY")
        @NotNull
        CommentType type,
        @Schema(description = "답글 생성시 소속될 댓글의 Id", nullable = true)
        @Positive
        Long parentId
) {

}
