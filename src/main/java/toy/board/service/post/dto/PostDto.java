package toy.board.service.post.dto;

import toy.board.domain.post.Post;

import java.time.LocalDateTime;

public record PostDto(
        Long postId,
        Long writerId,
        String writer,
        String title,
        String content,
        Long hits,
        LocalDateTime createdDate,
        boolean isEdited,
        int commentCount
) {
    public static PostDto of(final Post post) {
        return new PostDto(
                post.getId(),
                post.getWriterId(),
                post.getWriterNickname(),
                post.getTitle(),
                post.getContent(),
                post.getHits(),
                post.getCreatedDate(),
                post.isEdited(),
                post.commentCount()
        );
    }
}
