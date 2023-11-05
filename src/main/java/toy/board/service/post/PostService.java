package toy.board.service.post;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.comment.dto.CommentListDto;
import toy.board.repository.post.PostRepository;
import toy.board.repository.user.MemberRepository;
import toy.board.service.post.dto.PostDetailDto;
import toy.board.service.post.dto.PostDto;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long update(final String content, final Long postId, final Long memberId) {
        Post post = findPost(postId);
        Member member = findMember(memberId);
        post.update(content, member);
        return post.getId();
    }

    /**
     * PostDetail를 반환하는 메서드. Comment가 Comment 컬렉션을 내부적으로 가지고 있어
     * 엔티티 그래프를 통해 재귀적으로 프로퍼티에 접근하면 불필요한 쿼리가 발생하기 때문에
     * Post.Comment에 접근할 때는 Post.Comment.replies.replies에 접근하지 않도록 한다.
     * 때문에 재귀적인 방식은 사용하지 않는다.
     *
     * @param postId
     * @return
     */
    @Transactional
    public PostDetailDto getPostDetail(final Long postId) {
        // Post, Post.writer, Post.writer.profile, Profile.comments를 fetch join으로 가져옴
        Post post = findPostWithFetchComments(postId);

        post.increaseHits();

        CommentListDto commentListDto = CommentListDto.of(post);

        PostDto postDto = PostDto.of(post, commentListDto);

        return PostDetailDto.of(postDto, commentListDto);
    }

    @Transactional
    public Long create(final String title, final String content, final Long memberId) {
        Member member = findMember(memberId);

        Post post = new Post(member, title, content);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    /**
     * post 삭제 시 repository를 통해 직접 reply, comment를 우선 삭제하고 post를 삭제한다.
     * JPA에서 제공하는 OrhpanRemoval과 같은 기능을 사용하지 않는 이유는
     * 재귀적인 comment와 reply의 관계로 인해 count(comment + reply)만큼의 select 쿼리가 발생하기 때문이다.
     *
     * @param postId
     * @param memberId
     */
    @Transactional
    public void delete(final Long postId, final Long memberId) {
        Post post = findPost(postId);
        Member member = findMember(memberId);
        post.validateRight(member);
        commentRepository.deleteCommentsByPostAndType(post, CommentType.REPLY);
        commentRepository.deleteCommentsByPost(post);
        postRepository.delete(post);
    }


    private Member findMember(final Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() ->
                        new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND)
                );
    }

    private Post findPost(final Long postId) {
        return postRepository.findPostById(postId)
                .orElseThrow(() ->
                        new BusinessException(ExceptionCode.POST_NOT_FOUND)
                );
    }

    private Post findPostWithFetchComments(Long postId) {
        return postRepository.findPostByIdWithFetchComments(postId)
                .orElseThrow(() ->
                        new BusinessException(ExceptionCode.POST_NOT_FOUND)
                );
    }
}
