package toy.board.repository.comment.dto;

import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;

import java.time.LocalDateTime;
import java.util.List;

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
                comment.isModified(),
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
                comment.isModified(),
                comment.getCreatedDate(),
                null
        );
    }

    public boolean isCommentType() {
        return type == CommentType.COMMENT;
    }
}
