package toy.board.repository.post;

import static toy.board.domain.post.QComment.comment;
import static toy.board.domain.post.QPost.post;
import static toy.board.domain.user.QMember.member;
import static toy.board.domain.user.QProfile.profile;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.controller.post.dto.PostDto;
import toy.board.domain.post.Post;
import toy.board.repository.support.Querydsl4RepositorySupport;

public class PostRepositoryImpl extends Querydsl4RepositorySupport implements PostQueryRepository {

    protected PostRepositoryImpl() {
        super(Post.class);
    }

    @Override
    public Page<PostDto> findAllPost(Pageable pageable) {
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
    public Optional<PostDto> getPostDtoById(Long postId) {
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
                post.writerId,
                post.writer,
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