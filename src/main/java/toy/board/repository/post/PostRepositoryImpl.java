package toy.board.repository.post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Post;
import toy.board.repository.support.Querydsl4RepositorySupport;

import java.util.Optional;

import static toy.board.domain.post.QPost.post;
import static toy.board.domain.user.QMember.member;

public class PostRepositoryImpl extends Querydsl4RepositorySupport
        implements PostQueryRepository {

    public PostRepositoryImpl() {
        super(Post.class);
    }

    @Override
    public Optional<Post> findPostWithFetchJoinWriterAndProfileAndComments(Long postId) {
        return Optional.ofNullable(
                selectFromPostWithFetchJoinWriterAndProfile()
                        .leftJoin(post.comments).fetchJoin()
                        .where(equalsPostId(postId))
                        .fetchOne()
        );
    }

    /**
     * Post 반환 시 Member, Profile을 fetch join한다. ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고, 연계하여 fetch
     * join할 수 있다.
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public Optional<Post> findPostWithFetchJoinWriterAndProfile(Long postId) {
        return Optional.ofNullable(
                selectFromPostWithFetchJoinWriterAndProfile()
                        .where(equalsPostId(postId))
                        .fetchOne()
        );
    }

    /**
     * Post를 페이징 처리하여 Page<Post>로 반환한다. 이때 Member와 Profile을 fetch join한다.
     *
     * @param pageable the pageable to request a paged result,
     *                 can be {@link Pageable#unpaged()},
     *                 must not be {@literal null}.
     */
    @Override
    public Page<Post> findAllWithFetchJoinWriterAndProfile(Pageable pageable) {
        return applyPagination(
                pageable,
                contentQuery -> selectFromPostWithFetchJoinWriterAndProfile(),
                countQuery -> selectFromPostCount()
        );
    }

    /**
     * WriterId가 memberId와 같은 Post를 페이징 처리하여 Page<Post>로 반환한다. 이때 Member와 Profile을 fetch join한다.
     *
     * @param writerId writerId가 일치하는 Post들을 반환한다.
     * @param pageable 페이징 정보
     */
    @Override
    public Page<Post> findAllByWriterIdFetchJoinWriterAndProfile(Long writerId, Pageable pageable) {
        return applyPagination(
                pageable,
                contentQuery -> selectFromPostWithFetchJoinWriterAndProfile()
                        .where(equalsPostWriterId(writerId)),
                countQuery -> selectFromPostCount()
                        .where(equalsPostWriterId(writerId))
        );
    }

    private JPAQuery<Post> selectFromPostWithFetchJoinWriterAndProfile() {
        return selectFrom(post)
                .leftJoin(post.writer, member).fetchJoin()
                .leftJoin(member.profile).fetchJoin();
    }

    private JPAQuery<Long> selectFromPostCount() {
        return select(post.count())
                .from(post);
    }

    private static BooleanExpression equalsPostId(Long postId) {
        return post.id
                .eq(postId);
    }

    private static BooleanExpression equalsPostWriterId(Long writerId) {
        return post.writer.id.eq(writerId);
    }
}
