package toy.board.service.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;

@Schema(description = "댓글 컬렉션 응답 dto")
public record CommentsResponse(
        @Schema(description = "댓글 컬렉션")
        List<CommentResponse> comments,
        int count
) {

    public CommentsResponse(List<CommentResponse> commentDtos) {
        this(commentDtos, countTotalComment(commentDtos));
    }

    /**
     * Post 엔티티를 가져올 때 해당 Post의 모든 Comment를 fetch join으로 가져온다. Comment는 Comment와 Reply 두 타입으로 나뉜다. Reply 타입은 replies
     * 프로퍼티가 null이다. 만약 재귀적인 방식으로 List<Comment>를 CommentListDto로 변환하려 한다면 Reply.replies에 접근하게 되어 추가적인 쿼리가 발생한다.
     * <p>
     * 이를 방지하기 위해 Comment 타입의 CommentDto를 생성할 때는 Comment.replies를 CommentDto.createReplyTypeFrom()을 통해 Reply 타입
     * CommentListDto를 반환한다. CommentDto.createReplyTypeFrom() 내부에서는 Reply.replies에 접근하지 않는다.
     *
     * @param post 해당 post의 Comment를 CommentListDto로 변환
     * @return 타입에 따라 계층적으로 구성된 CommentListDto
     */
    public static CommentsResponse of(Post post) {
        return new CommentsResponse(
                post.getComments()
                        .stream()
                        .filter(Comment::isCommentType)
                        .map(CommentResponse::createCommentTypeFrom)
                        .toList(),
                post.commentCount()
        );
    }

    private static int countTotalComment(List<CommentResponse> commentDtos) {
        return commentDtos.size() + calculateRepliesCount(commentDtos);
    }

    private static int calculateRepliesCount(List<CommentResponse> commentDtos) {
        AtomicInteger countTotal = new AtomicInteger();
        commentDtos.stream()
                .filter(CommentResponse::isCommentType)
                .forEach(c ->
                        countTotal.addAndGet(
                                c.replies()
                                        .count()
                        )
                );
        return countTotal.get();
    }
}
