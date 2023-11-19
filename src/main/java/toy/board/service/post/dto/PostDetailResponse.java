package toy.board.service.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시물 상세 조회 DTO")
public record PostDetailResponse(

        @Schema(description = "게시물 상세")
        PostResponse post,
        @Schema(description = "댓글 목록")
        CommentsResponse comments
) {

}
