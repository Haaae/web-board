package toy.board.service;

import java.util.Optional;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Post;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.repository.profile.ProfileRepository;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final CommentRepository commentRepository;

    public Long update(String content, Long postId, Long memberId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND));

        post.update(content, memberId);

        return post.getId();
    }

    @Transactional
    public Long create(String title, String content, Long memberId) {
        String nickname = profileRepository.findNicknameByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND));

        Post post = new Post(memberId, nickname, title, content);

        postRepository.save(post);

        return post.getId();
    }

    public void delete(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND));
        post.validateRight(memberId);
        commentRepository.deleteCommentsByPost(post);
        postRepository.deleteById(postId);
    }
}
