package toy.board.controller.mypage.dto;

import toy.board.domain.post.Post;

import java.time.LocalDateTime;

public record MyPageDto(
        Long postId,
        String title,
        String content,
        String writer,
        Long hits,
        LocalDateTime createdDate,
        int commentCount
) {

    public static MyPageDto of(Post post) {
        return new MyPageDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getWriterNickname(),
                post.getHits(),
                post.getCreatedDate(),
                post.commentCount()
        );
    }
}
