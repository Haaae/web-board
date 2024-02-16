package toy.board.controller.mypage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import toy.board.domain.post.Post;

@Schema(description = "사용자 작성 게시물 응답 DTO")
public record MyPostResponse(

        @Schema(description = "게시물 Id")
        Long postId,
        @Schema(description = "제목")
        String title,
        @Schema(description = "본문")
        String content,
        @Schema(description = "작성자 닉네임", nullable = true)
        String writer,
        @Schema(description = "조회수")
        Long hits,
        @Schema(description = "생성 일자", example = "2021-11-08T11:44:30.327959")
        LocalDateTime createdDate,
        @Schema(description = "게시물의 댓글 개수")
        int commentCount
) {

    public static MyPostResponse of(Post post) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getWriterNickname(),
                post.getHits(),
                post.getCreatedDate(),
                post.countComments()
        );
    }
}
