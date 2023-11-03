package toy.board.controller.mypage.dto;

import toy.board.domain.post.Comment;

import java.time.LocalDateTime;

public record MyCommentDto(
        Long commentId,
        String content,
        LocalDateTime createdDate,
        Long postId
) {

    public static MyCommentDto of(Comment comment) {
        return new MyCommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedDate(),
                comment.getPostId()
        );
    }
}
