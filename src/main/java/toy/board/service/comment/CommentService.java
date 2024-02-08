package toy.board.service.comment;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.repository.user.MemberRepository;

@Service
@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long create(
            final String content,
            final CommentType type,
            final Optional<Long> parentId,
            final Long postId,
            final Long memberId
    ) {

        Post post = postRepository.findPostWithFetchJoinWriter(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        // parentId가 null이 아니면 해당 Comment를 찾아온다. parentId가 Optional.empty()인 경우 Optional.empty()를 그대로 반환한다.
        // 그후 orElse()를 통해 Optional 값을 가져온다. 만약 정상적으로 parentComment 객체를 저장소에서 가져오면 해당 객체가 할당된다.
        Comment parentComment = parentId.flatMap(commentRepository::findCommentWithFetchJoinWriter)
                .orElse(null);

        Comment comment = new Comment(post, member, content, type, parentComment);

        commentRepository.save(comment);
        return comment.getId();
    }

    @Transactional
    public Long update(final Long commentId, final String content, final Long memberId) {

        Comment comment = commentRepository.findCommentWithFetchJoinWriter(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        comment.update(content, member);
        return commentId;
    }

    /**
     * Comment 삭제 시 DB에서 삭제하지 않고 isDeleted = true로 변경한다.
     *
     * @param commentId
     * @param memberId
     */
    @Transactional
    public void delete(final Long commentId, final Long memberId) {

        Comment comment = commentRepository.findCommentWithFetchJoinWriter(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        comment.deleteBy(member);
    }
}
