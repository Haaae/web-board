package toy.board.repository.comment;

import static toy.board.domain.post.QComment.comment;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Comment;
import toy.board.repository.support.Querydsl4RepositorySupport;

public class CommentRepositoryImpl extends Querydsl4RepositorySupport
        implements CommentQueryRepository {

    public CommentRepositoryImpl() {
        super(Comment.class);
    }

    /**
     * Comment 반환 시 Member를 fetch join한다. ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고, 연계하여 fetch join할 수 있다.
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public Optional<Comment> findCommentWithFetchJoinWriter(final Long id) {
        return Optional.ofNullable(
                selectFrom(comment)
                        .leftJoin(comment.writer).fetchJoin()
                        .where(comment.id.eq(id))
                        .fetchOne()
        );
    }

    /**
     * WriterId가 memberId와 같은 Comment를 페이징 처리하여 Page<Post>로 반환한다. 이때 Member와 Profile, Post를 fetch join한다.
     *
     * @param writerId writerId가 일치하는 Post들을 반환한다.
     * @param pageable 페이징 정보
     */
    @Override
    public Page<Comment> findAllNotDeletedCommentByWriterIdWithFetchJoinPostAndWriter(
            final Long writerId, final Pageable pageable) {
        return applyPagination(
                pageable,

                contentQuery -> selectFrom(comment)
                        .leftJoin(comment.writer).fetchJoin()
                        .leftJoin(comment.post).fetchJoin()
                        .where(
                                comment.writer.id.eq(writerId)
                                        .and(comment.isDeleted.isFalse())
                        ),

                countQuery -> select(comment.count())
                        .from(comment)
                        .where(
                                comment.writer.id.eq(writerId)
                        )
        );
    }
}
