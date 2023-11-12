package toy.board.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "게시물 생성, 수정, 삭제 응답 DTO")
public record PostIdDto(
        @Schema(description = "게시물 Id")
        @Positive
        Long postId
) {
    public static PostIdDto from(Long postId) {
        return new PostIdDto(postId);
    }
}
