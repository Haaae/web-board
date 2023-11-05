package toy.board.service.comment;

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

import java.util.Optional;

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
        Post post = findPost(postId);
        Member member = findMember(memberId);
        Comment parentComment = parentId.map(this::findComment)
                .orElse(null);  // parentId가 null이 아니면 해당 Comment를 찾아온다.

        Comment comment = new Comment(post, member, content, type, parentComment);

        commentRepository.save(comment);
        return comment.getId();
    }

    @Transactional
    public Long update(final Long commentId, final String content, final Long memberId) {
        Comment comment = findComment(commentId);
        Member member = findMember(memberId);

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
        Comment comment = findComment(commentId);
        Member member = findMember(memberId);
        comment.validateRight(member);
        comment.delete();
    }

    private Post findPost(final Long postId) {
        return postRepository.findPostWithFetchJoinWriterAndProfile(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND));
    }

    private Comment findComment(final Long commentId) {
        return commentRepository.findCommentById(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COMMENT_NOT_FOUND));
    }

    private Member findMember(final Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND));
    }
}
