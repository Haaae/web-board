package toy.board.service.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import toy.board.repository.comment.dto.CommentListDto;

@Schema(description = "게시물 상세 조회 DTO")
public record PostDetailDto(

        @Schema(description = "게시물 상세")
        PostDto post,
        @Schema(description = "댓글 목록")
        CommentListDto comments
) {

}
