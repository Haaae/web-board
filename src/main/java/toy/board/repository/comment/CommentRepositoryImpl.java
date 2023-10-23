package toy.board.repository.comment;

import static toy.board.domain.post.QComment.comment;

import java.util.Comparator;
import java.util.List;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.QComment;
import toy.board.repository.comment.dto.CommentDto;
import toy.board.repository.comment.dto.CommentListDto;
import toy.board.repository.support.Querydsl4RepositorySupport;

public class CommentRepositoryImpl extends Querydsl4RepositorySupport
        implements CommentQueryRepository {

    public CommentRepositoryImpl() {
        super(Comment.class);
    }

    @Override
    public CommentListDto getCommentListDtoByPostId(final Long postId) {
        return new CommentListDto(
                getComments(postId)
                        .stream()
                        .map(this::convertToDto)
                        .toList()
        );
    }

    private List<Comment> getComments(final Long postId) {
        QComment reply = new QComment("reply");
        return selectFrom(comment)
                .leftJoin(comment.replies, reply).fetchJoin()
                .where(
                        comment.post.id.eq(postId),
                        comment.type.eq(CommentType.COMMENT)
                )
                .orderBy(comment.createdDate.asc())
                .fetch();
    }

    private CommentDto convertToDto(final Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getWriter().getId(),
                comment.getWriter().getProfile().getNickname(),
                comment.getContent(),
                comment.getType(),
                comment.isDeleted(),
                comment.isModified(),
                comment.getCreatedDate(),
                comment.getReplies().stream().sorted(
                        Comparator.comparing(Comment::getCreatedDate)
                ).map(reply ->
                        new CommentDto(
                                reply.getId(),
                                reply.getWriter().getId(),
                                reply.getWriter().getProfile().getNickname(),
                                reply.getContent(),
                                reply.getType(),
                                reply.isDeleted(),
                                reply.isModified(),
                                reply.getCreatedDate(),
                                null)
                ).toList()
        );
    }
}
