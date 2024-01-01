package toy.board.service.comment;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long create(final String content, final CommentType type,
                       final Optional<Long> parentId, final Long postId, final Long memberId) {
        Post post = findPostWithFetchJoinWriterAndProfile(postId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        Comment parentComment = parentId.map(this::findCommentWithFetchJoinWriterAndProfile)
                .orElse(null);  // parentId가 null이 아니면 해당 Comment를 찾아온다.

        Comment comment = new Comment(post, member, content, type, parentComment);

        commentRepository.save(comment);
        return comment.getId();
    }

    @Transactional
    public Long update(final Long commentId, final String content, final Long memberId) {
        Comment comment = findCommentWithFetchJoinWriterAndProfile(commentId);
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
        Comment comment = findCommentWithFetchJoinWriterAndProfile(commentId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));

        comment.validateRight(member);
        comment.delete();
    }

    private Post findPostWithFetchJoinWriterAndProfile(final Long postId) {
        return postRepository.findPostWithFetchJoinWriter(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));
    }

    private Comment findCommentWithFetchJoinWriterAndProfile(final Long commentId) {
        return commentRepository.findCommentWithFetchJoinWriterAndProfile(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND));
    }
}
