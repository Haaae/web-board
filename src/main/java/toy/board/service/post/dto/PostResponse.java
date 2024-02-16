package toy.board.service.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import toy.board.domain.post.Post;

@Schema(description = "게시물 조회 정보 DTO")
public record PostResponse(
        @Schema(description = "게시물 Id")
        @Positive
        Long postId,
        @Schema(description = "작성자 Id", nullable = true)
        @Positive
        Long writerId,
        @Schema(description = "작성자 닉네임", example = "nickname", nullable = true)
        String writer,
        @Schema(description = "제목")
        String title,
        @Schema(description = "본문")
        String content,
        @Schema(description = "조회수")
        Long hits,
        @Schema(description = "생성일", example = "2021-11-08T11:44:30.327959")
        LocalDateTime createdDate,
        @Schema(description = "수정여부")
        boolean isEdited,
        @Schema(description = "댓글수")
        int commentCount
) {
    public static PostResponse of(final Post post) {
        return new PostResponse(
                post.getId(),
                post.getWriterId(),
                post.getWriterNickname(),
                post.getTitle(),
                post.getContent(),
                post.getHits(),
                post.getCreatedDate(),
                post.isEdited(),
                post.countComments()
        );
    }
}
