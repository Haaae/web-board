package toy.board.controller.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Post;

public record PostUpdateDto(
        @NotBlank
        @Size(max = Post.CONTENT_MAX_LENGTH)
        String content
) {

}