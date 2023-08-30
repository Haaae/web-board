package toy.board.service;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.repository.profile.ProfileRepository;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;

    public Long create(final String content, final CommentType type,
                       final Long parentId, final Long postId, final Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND));

        String nickname = profileRepository.findNicknameByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND));

        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COMMENT_NOT_FOUND));

        Comment comment = new Comment(post, memberId, nickname, content, type, parentComment);

        commentRepository.save(comment);

        return comment.getId();
    }

    public Long update(final Long commentId, final String content, final Long memberId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COMMENT_NOT_FOUND));

        comment.update(content, memberId);

        return commentId;
    }

    public void delete(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COMMENT_NOT_FOUND));
        comment.validateRight(memberId);
        commentRepository.deleteById(comment.getId());
    }
}
