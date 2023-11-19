package toy.board.controller.post.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "게시물 생성, 수정, 삭제 응답 DTO")
public record PostIdResponse(
        @Schema(description = "게시물 Id")
        @Positive
        Long postId
) {
    public static PostIdResponse from(Long postId) {
        return new PostIdResponse(postId);
    }
}
