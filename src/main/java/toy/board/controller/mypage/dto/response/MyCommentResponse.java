package toy.board.controller.mypage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import toy.board.domain.post.Comment;

@Schema(description = "사용자 작성 댓글 응답 DTO")
public record MyCommentResponse(
        @Schema(description = "댓글 Id")
        Long commentId,
        @Schema(description = "본문")
        String content,
        @Schema(description = "생성 일자", example = "2021-11-08T11:44:30.327959")
        LocalDateTime createdDate,
        @Schema(description = "게시물 Id")
        Long postId
) {

    public static MyCommentResponse of(Comment comment) {
        return new MyCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedDate(),
                comment.getPostId()
        );
    }
}
