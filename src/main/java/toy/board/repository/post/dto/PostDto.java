package toy.board.repository.post.dto;

import java.time.LocalDateTime;
import toy.board.domain.post.Post;

public record PostDto(
        Long postId,
        Long writerId,
        String writer,
        String title,
        String content,
        Long hits,
        LocalDateTime createdDate,
        boolean isModified,
        int commentNum
) {
    public static PostDto of(Post post) {
        return new PostDto(
                post.getId(),
                post.getWriterId(),
                post.getWriterNickname(),
                post.getTitle(),
                post.getContent(),
                post.getHits(),
                post.getCreatedDate(),
                post.isModified(),
                post.countComment()
        );
    }
}
