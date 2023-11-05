package toy.board.repository.comment;

import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.QComment;
import toy.board.domain.user.QMember;
import toy.board.domain.user.QProfile;
import toy.board.repository.comment.dto.CommentDto;
import toy.board.repository.comment.dto.CommentListDto;
import toy.board.repository.support.Querydsl4RepositorySupport;

import java.util.List;

import static toy.board.domain.post.QComment.comment;
import static toy.board.domain.user.QMember.member;
import static toy.board.domain.user.QProfile.profile;

public class CommentRepositoryImpl extends Querydsl4RepositorySupport
        implements CommentQueryRepository {

    public CommentRepositoryImpl() {
        super(Comment.class);
    }

    /**
     * 내부적으로 postId에 해당하는 List<Comment>를 가져와 CommentListDto로 변환한다.
     * 이때 List<Comment> 요소의 모든 reply과 Member, Profile은 물론
     * reply의 Member, Profile도 fetch join으로 가져온다.
     * comment.replies는 fetch join으로 가져왔어도
     * comment.replies의 요소인 reply.replies에 접근하면 추가적인 쿼리가 총 Comment.replies.size()만큼 발생한다.
     * 내부적으로 Comment.replies.replies에 접근하지 않아 불필요한 쿼리가 발생하지 않는다.
     *
     * @param postId
     * @return postId에 해당하는 모든 CommentListDto
     */
    @Override
    public CommentListDto getCommentListDtoByPostId(final Long postId) {
        return new CommentListDto(
                findComments(postId)
                        .stream()
                        .map(this::convertToDto)
                        .toList()
        );
    }

    /**
     * Comment와 1대1 매핑된 Member, Profile을 제약없이 fetch join으로 가져온다.
     * Comment.replies의 요소들과 매핑된 Member, Profile도 가져온다.
     *
     * @param postId
     * @return List<Comment>
     */
    private List<Comment> findComments(final Long postId) {
        QComment reply = new QComment("reply");
        QMember replyWriter = new QMember("replyWriter");
        QProfile replyProfile = new QProfile("replyProfile");
        return selectFrom(comment)
                .leftJoin(comment.writer, member).fetchJoin()
                .leftJoin(member.profile, profile).fetchJoin()
                .leftJoin(comment.replies, reply).fetchJoin()
                .leftJoin(reply.writer, replyWriter).fetchJoin()
                .leftJoin(replyWriter.profile, replyProfile).fetchJoin()
                .where(
                        comment.post.id.eq(postId),
                        comment.type.eq(CommentType.COMMENT)
                )
                .orderBy(comment.createdDate.asc())
                .fetch();
    }

    /**
     * Comment를 CommentDto로 변환하는 메서드. comment.replies는 fetch join으로 가져왔어도
     * comment.replies의 요소인 reply.replies에 접근하면 추가적인 쿼리가 총 Comment.replies.size()만큼 발생한다.
     * 따라서 Comment를 CommentDto로 만들 때 reply타입 Comment는 replies를 null로 지정 전달하여 불필요한 쿼리를 막는다.
     * 애초에 해당 메서드가 필요한 이유도 재귀적으로 엔티티 그래프를 사용하면 불필요한 쿼리가 발생하는 것을 피할 수 없기 때문이다.
     *
     * @param comment
     * @return CommentDto
     */
    private CommentDto convertToDto(final Comment comment) {
//        return new CommentDto(
//                comment.getId(),
//                comment.getWriterId(),
//                comment.getWriterNickname(),
//                comment.getContent(),
//                comment.getType(),
//                comment.isDeleted(),
//                comment.isModified(),
//                comment.getCreatedDate(),
//                new CommentListDto(
//                        comment.getReplies().stream().sorted(
//                                Comparator.comparing(Comment::getCreatedDate)
//                        ).map(reply ->
//                                new CommentDto(
//                                        reply.getId(),
//                                        reply.getWriterId(),
//                                        reply.getWriterNickname(),
//                                        reply.getContent(),
//                                        reply.getType(),
//                                        reply.isDeleted(),
//                                        reply.isModified(),
//                                        reply.getCreatedDate(),
//                                        null)
//                        ).toList()
//                )
//        );
        return CommentDto.createCommentTypeFrom(comment);
    }
}
