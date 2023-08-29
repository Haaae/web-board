package toy.board.repository.comment;

import static toy.board.domain.post.QComment.comment;

import java.util.List;
import toy.board.controller.post.dto.CommentDto;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.QComment;
import toy.board.repository.support.Querydsl4RepositorySupport;

public class CommentRepositoryImpl extends Querydsl4RepositorySupport implements
        CommentQueryRepository {

    public CommentRepositoryImpl() {
        super(Comment.class);
    }

    @Override
    public List<CommentDto> findCommentsConvertedToDtoUsingPostId(Long postId) {
        return getComments(postId).stream().map(this::convertToDto).toList();
    }

    private List<Comment> getComments(Long postId) {
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

    private CommentDto convertToDto(Comment comment) {
        CommentDto commentDto = new CommentDto(
                comment.getId(),
                comment.getWtiterId(),
                comment.getWriter(),
                comment.getContent(),
                comment.isDeleted(),
                comment.isModified(),
                comment.getCreatedDate(),
                comment.getReplies().stream().map(reply ->
                        new CommentDto(
                                reply.getId(),
                                reply.getWtiterId(),
                                reply.getWriter(),
                                reply.getContent(),
                                reply.isDeleted(),
                                reply.isModified(),
                                reply.getCreatedDate(),
                                null)
                ).toList()
        );
        commentDto.replies().stream().sorted();
        return commentDto;
    }
}
