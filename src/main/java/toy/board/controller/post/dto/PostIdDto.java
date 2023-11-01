package toy.board.controller.post.dto;

public record PostIdDto(
        Long postId
) {
    public static PostIdDto from(Long postId) {
        return new PostIdDto(postId);
    }
}
