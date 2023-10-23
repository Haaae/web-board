package toy.board.repository.post;

import static toy.board.domain.post.QComment.comment;
import static toy.board.domain.post.QPost.post;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Post;
import toy.board.repository.post.dto.PostDto;
import toy.board.repository.support.Querydsl4RepositorySupport;

public class PostRepositoryImpl extends Querydsl4RepositorySupport
        implements PostQueryRepository {

    public PostRepositoryImpl() {
        super(Post.class);
    }

    @Override
    public Page<PostDto> findAllPost(final Pageable pageable) {
        return applyPagination(pageable,
                contentQuery -> contentQuery
                        .select(getPostDtoConstructorExpression())
                        .from(post)
                        .leftJoin(comment).on(comment.post.eq(post))
                        .groupBy(post)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize()),

                countQuery -> countQuery
                        .select(post.count())
                        .from(post)
        );
    }

    @Override
    public Optional<PostDto> getPostDtoById(final Long postId) {
        return Optional.ofNullable(
                select(getPostDtoConstructorExpression())
                        .from(post)
                        .where(post.id.eq(postId))
                        .leftJoin(comment).on(comment.post.eq(post))
                        .groupBy(post)
                        .fetchOne()
        );
    }

    private ConstructorExpression<PostDto> getPostDtoConstructorExpression() {
        return Projections.constructor(PostDto.class,
                post.id.as("postId"),
                post.writer.id,
                post.writer.profile.nickname,
                post.title,
                post.content,
                post.hits,
                post.createdDate,
                new CaseBuilder()
                        .when(post.createdDate.eq(post.lastModifiedDate))
                        .then(false)
                        .otherwise(true)
                        .as("isModified"),
                comment.count().as("commentNum")
        );
    }
}
