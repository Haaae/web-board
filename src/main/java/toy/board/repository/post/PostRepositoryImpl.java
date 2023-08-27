package toy.board.repository.post;

import static toy.board.domain.post.QComment.comment;
import static toy.board.domain.post.QPost.post;
import static toy.board.domain.user.QMember.member;
import static toy.board.domain.user.QProfile.profile;

import com.querydsl.core.types.Projections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.controller.post.dto.PostListDto;
import toy.board.domain.post.Post;
import toy.board.repository.support.Querydsl4RepositorySupport;

public class PostRepositoryImpl extends Querydsl4RepositorySupport implements PostQueryRepository {

    protected PostRepositoryImpl() {
        super(Post.class);
    }

    @Override
    public Page<PostListDto> findAllPost(Pageable pageable) {
        return applyPagination(pageable,
                contentQuery -> contentQuery
                        .select(Projections.constructor(PostListDto.class,
                                post.id.as("postId"),
                                member.id.as("writerId"),
                                profile.nickname.as("writer"),
                                post.title,
                                post.content,
                                post.hits,
                                post.createdDate,
                                comment.count().as("commentNum")
                        ))
                        .from(post)
                        .leftJoin(post.member, member)
                        .leftJoin(member.profile, profile)
                        .leftJoin(comment).on(comment.post.eq(post))
                        .groupBy(post)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize()),

                countQuery -> countQuery
                        .select(post.count())
                        .from(post)
        );
    }
}
