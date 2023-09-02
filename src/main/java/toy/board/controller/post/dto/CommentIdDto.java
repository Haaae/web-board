package toy.board.controller.post.dto;

public record CommentIdDto(
        Long commentId
) {
    public static CommentIdDto of(Long commentId) {
        return new CommentIdDto(commentId);
    }
}
