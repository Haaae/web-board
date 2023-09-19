package toy.board.controller.post.dto;

public record PostIdDto(
        Long postId
) {
    public static PostIdDto of(Long postId) {
        return new PostIdDto(postId);
    }
}
