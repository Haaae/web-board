package toy.board.controller.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Comment;

public record CommentUpdateDto(
        @NotBlank
        @Size(max = Comment.CONTENT_LENGTH)
        String content
) {

}
