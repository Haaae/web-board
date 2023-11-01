package toy.board.repository.comment.dto;

import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;

import java.time.LocalDateTime;

public record CommentDto(
        Long commentId,
        Long writerId,
        String writer,
        String content,
        CommentType type,
        boolean isDeleted,
        boolean isModified,
        LocalDateTime createdDate,
        CommentListDto replies
) {

    /**
     * Comment의 프로퍼티로 CommentDto를 생성해 반환한다. 이때 Comment.replies에 접근하지 않고 CommentDto.replies를 null로 설정한다.
     *
     * @param comment
     * @return replies가 null인 CommentDto
     */
    public static CommentDto createHasNotReplies(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getWriterId(),
                comment.getWriterNickname(),
                comment.getContent(),
                comment.getType(),
                comment.isDeleted(),
                comment.isModified(),
                comment.getCreatedDate(),
                null
        );
    }

    public boolean isCommentType() {
        return type == CommentType.COMMENT;
    }
}
