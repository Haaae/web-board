package toy.board.controller.post.dto;

import java.time.LocalDateTime;
import toy.board.domain.post.Post;

public record PostListDto(
        Long postId,
        Long writerId,
        String writer,
        String title,
        String content,
        Long hits,
        LocalDateTime createdDate,
        Long commentNum
) {

    public PostListDto(Post post, Long commentNum) {
        this(
                post.getId(),
                post.getMember().getId(),
                post.getMember().getProfile().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getHits(),
                post.getCreatedDate(),
                commentNum
        );
    }
}
