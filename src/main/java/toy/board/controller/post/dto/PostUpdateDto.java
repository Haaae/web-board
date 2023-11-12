package toy.board.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Post;

@Schema(description = "게시물 수정 요청 DTO")
public record PostUpdateDto(
        @Schema(description = "본문")
        @NotBlank
        @Size(max = Post.CONTENT_MAX_LENGTH)
        String content
) {

}
