package toy.board.repository.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import toy.board.domain.post.Post;
import toy.board.repository.comment.dto.CommentDto;

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
    public static PostDto of(Post post, List<CommentDto> commentDtos) {
        AtomicLong commentNum = new AtomicLong(commentDtos.size());

        commentDtos.forEach(c ->
                commentNum.addAndGet(c.replies().size())
        );

        return new PostDto(
                post.getId(),
                post.getWriterId(),
                post.getWriter(),
                post.getTitle(),
                post.getContent(),
                post.getHits(),
                post.getCreatedDate(),
                post.isModified(),
                commentNum.get()
        );
    }
}
