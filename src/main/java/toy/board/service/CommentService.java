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

    public Long create(String content, CommentType type, Long parentId, Long postId, Long memberId) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<String> nickname = profileRepository.findNicknameByMemberId(memberId);
        Optional<Comment> parentComment = commentRepository.findById(parentId);

        Comment comment = type.createComment(
                post.orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND)),
                memberId,
                nickname.orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND)),
                content,
                parentComment.orElseThrow(() -> new BusinessException(ExceptionCode.COMMENT_NOT_FOUND))
        );

        commentRepository.save(comment);

        return comment.getId();
    }
}
