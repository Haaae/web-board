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
        Long commentNum
) {
    public static PostDto of(Post post, long commentNum) {
        return new PostDto(
                post.getId(),
                post.getWriterId(),
                post.getWriter(),
                post.getTitle(),
                post.getContent(),
                post.getHits(),
                post.getCreatedDate(),
                post.isModified(),
                commentNum
        );
    }
}
