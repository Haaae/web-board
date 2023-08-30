package toy.board.controller.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;

public record CommentCreationRequest(
        @NotBlank
        @Size(max = Comment.CONTENT_LENGTH)
        String content,
        @NotNull
        CommentType type,
        Long parentId
) {

}
