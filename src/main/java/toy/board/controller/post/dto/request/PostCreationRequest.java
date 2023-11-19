package toy.board.controller.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.post.Post;

@Schema(description = "게시물 생성 요청 DTO")
public record PostCreationRequest(
        @Schema(description = "제목")
        @NotBlank
        @Size(max = Post.TITLE_MAX_LENGTH)
        String title,
        @Schema(description = "본문")
        @NotBlank
        @Size(max = Post.CONTENT_MAX_LENGTH)
        String content
) {

}
