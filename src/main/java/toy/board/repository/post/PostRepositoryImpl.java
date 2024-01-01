package toy.board.repository.post;

import static toy.board.domain.post.QPost.post;
import static toy.board.domain.user.QMember.member;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Post;
import toy.board.repository.support.Querydsl4RepositorySupport;

public class PostRepositoryImpl extends Querydsl4RepositorySupport
        implements PostQueryRepository {

    public PostRepositoryImpl() {
        super(Post.class);
    }

    /**
     * Post 반환 시 Member, Profile, comments을 fetch join한다. ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고, 연계하여 fetch join할 수
     * 있다. 하나의 켈렉션에 대해 fetch join할 수 있다.
     *
     * @param postId must not be {@literal null}.
     * @return
     */
    @Override
    public Optional<Post> findPostWithFetchJoinWriterAndComments(final Long postId) {
        return Optional.ofNullable(
                selectFrom(post)
                        .leftJoin(post.writer, member).fetchJoin()
                        .leftJoin(post.comments)
                        .fetchJoin()    // default join fetch로 가져올 수도 있지만, fetch join이 가능하므로 사용하도록 한다.
                        .where(post.id
                                .eq(postId)
                        )
                        .fetchOne()
        );
    }

    /**
     * Post 반환 시 Member, Profile을 fetch join한다. ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고, 연계하여 fetch join할 수 있다.
     *
     * @param postId must not be {@literal null}.
     * @return
     */
    @Override
    public Optional<Post> findPostWithFetchJoinWriter(final Long postId) {
        return Optional.ofNullable(
                selectFrom(post)
                        .leftJoin(post.writer, member).fetchJoin()
                        .where(post.id
                                .eq(postId)
                        )
                        .fetchOne()
        );
    }

    /**
     * Post를 페이징 처리하여 Page<Post>로 반환한다. 이때 Member와 Profile을 fetch join한다.
     *
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be
     *                 {@literal null}.
     */
    @Override
    public Page<Post> findAllWithFetchJoinWriter(final Pageable pageable) {
        return applyPagination(
                pageable,
                contentQuery -> selectFrom(post)
                        .leftJoin(post.writer, member).fetchJoin(),
                countQuery -> select(post.count())
                        .from(post)
        );
    }

    /**
     * WriterId가 memberId와 같은 Post를 페이징 처리하여 Page<Post>로 반환한다. 이때 Member와 Profile을 fetch join한다.
     *
     * @param writerId writerId가 일치하는 Post들을 반환한다.
     * @param pageable 페이징 정보
     */
    @Override
    public Page<Post> findAllByWriterIdWithFetchWriter(final Long writerId, final Pageable pageable) {
        return applyPagination(
                pageable,
                contentQuery -> selectFrom(post)
                        .leftJoin(post.writer, member).fetchJoin()
                        .where(
                                post.writer.id.eq(writerId)
                        ),
                countQuery -> select(post.count())
                        .from(post)
                        .where(
                                post.writer.id.eq(writerId)
                        )
        );
    }
}
