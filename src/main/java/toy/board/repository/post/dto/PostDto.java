package toy.board.repository.post.dto;

import toy.board.domain.post.Post;
import toy.board.repository.comment.dto.CommentListDto;

import java.time.LocalDateTime;

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

    public static PostDto of(Post post, CommentListDto commentListDto) {
        return new PostDto(
                post.getId(),
                post.getWriterId(),
                post.getWriterNickname(),
                post.getTitle(),
                post.getContent(),
                post.getHits(),
                post.getCreatedDate(),
                post.isModified(),
                commentListDto.countTotalCommentNum()
        );
    }
}
