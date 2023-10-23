package toy.board.service.post;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.comment.dto.CommentListDto;
import toy.board.repository.post.PostRepository;
import toy.board.repository.post.dto.PostDto;
import toy.board.repository.profile.ProfileRepository;
import toy.board.repository.user.MemberRepository;
import toy.board.service.post.dto.PostDetailDto;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class PostService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long update(final String content, final Long postId, final Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND));
        post.update(content, memberId);
        return post.getId();
    }

    @Transactional
    public PostDetailDto getPostDetail(final Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND));

        post.increaseHits();

        CommentListDto commentListDto = commentRepository.getCommentListDtoByPostId(postId);

        PostDto postDto = PostDto.of(post, commentListDto.getTotalCommentNum());

        return PostDetailDto.of(postDto, commentListDto);
    }

    @Transactional
    public Long create(final String title, final String content, final Long memberId) {
        Member findMember = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND));

        Post post = new Post(findMember, title, content);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Transactional
    public void delete(final Long postId, final Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.POST_NOT_FOUND));
        post.validateRight(memberId);
        commentRepository.deleteCommentsByPost(post);
        postRepository.deleteById(postId);
    }
}
