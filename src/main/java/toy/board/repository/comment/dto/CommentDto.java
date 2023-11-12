package toy.board.repository.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;

@Schema(description = "댓글 조회 정보 DTO")
public record CommentDto(
        @Schema(description = "댓글 Id", example = "1")
        @Positive
        Long commentId,
        @Schema(description = "작성자 Id", example = "1", nullable = true)
        @Positive
        Long writerId,
        @Schema(description = "작성자 닉네임", nullable = true)
        String writer,
        @Schema(description = "본문")
        String content,
        @Schema(description = "댓글 타입:댓글, 답글", example = "REPLY")
        CommentType type,
        @Schema(description = "삭제 여부")
        boolean isDeleted,
        @Schema(description = "수정 여부")
        boolean isEdited,
        @Schema(description = "생성 일자", example = "2021-11-08T11:44:30.327959")
        LocalDateTime createdDate,
        @Schema(description = "답글 목록", nullable = true)
        CommentListDto replies
) {

    /**
     * replies를 갖는 CommentDto를 생성해 반환한다.
     *
     * @param comment CommentType.COMMENT인 Comment
     * @return replies가 존재하는 CommentDto
     */
    public static CommentDto createCommentTypeFrom(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getWriterId(),
                comment.getWriterNickname(),
                comment.getContent(),
                comment.getType(),
                comment.isDeleted(),
                comment.isEdited(),
                comment.getCreatedDate(),
                new CommentListDto(
                        convertRepliesToCommentDtoCollection(comment.getReplies())
                )
        );
    }

    private static List<CommentDto> convertRepliesToCommentDtoCollection(List<Comment> replies) {
        return replies.stream()
                .map(CommentDto::createReplyTypeFrom)
                .toList();
    }

    /**
     * Comment의 프로퍼티로 CommentDto를 생성해 반환한다. 이때 Comment.replies에 접근하지 않고 CommentDto.replies를 null로 설정한다.
     *
     * @param comment CommentType.REPLY인 Comment를 비롯한 replies에 접근할 필요없는 모든 Comment
     * @return replies가 null인 CommentDto
     */
    public static CommentDto createReplyTypeFrom(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getWriterId(),
                comment.getWriterNickname(),
                comment.getContent(),
                comment.getType(),
                comment.isDeleted(),
                comment.isEdited(),
                comment.getCreatedDate(),
                null
        );
    }

    public boolean isCommentType() {
        return type == CommentType.COMMENT;
    }
}
