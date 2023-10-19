package toy.board.service.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.repository.profile.ProfileRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ProfileRepository profileRepository;

    Long invalidPostId = 1242L;
    Long invalidMemberId = 23523L;
    String newContent = "new content";
    String content = "content";
    String title = "title";
    Long postId = 1L;
    Long memberId = 1L;

    @BeforeEach
    void init() {
        Member member = MemberTest.create();
        Post post = new Post(memberId, member.getUsername(), title, content);

        given(postRepository.findById(anyLong())).willReturn(Optional.empty());
        given(postRepository.findById(eq(postId))).willReturn(Optional.of(post));
        given(commentRepository.getCommentDtosByPostId(postId)).willReturn(new ArrayList<>());

        given(profileRepository.findNicknameByMemberId(anyLong())).willReturn(
                Optional.empty());
        given(profileRepository.findNicknameByMemberId(eq(memberId))).willReturn(
                Optional.of(member.getProfile().getNickname()));
    }

    @DisplayName("게시물 조회 시 게시물 조회 수 증가: 성공")
    @Test
    public void whenReadPostDetail_thenIncreasePostHits() throws  Exception {
        //given
        Long expectedHits = 1L;

        //when
        postService.getPostDetail(postId);
        Post post = postRepository.findById(postId).get();

        //then
        assertThat(post.getHits()).isEqualTo(expectedHits);
    }

    @DisplayName("update 성공 시 update post id 반환")
    @Test
    public void whenPostUpdate_thenReturnValidValue() throws  Exception {
        //given
        //when
        postService.update(newContent, postId, memberId);
        Post post = postRepository.findById(postId).get();
        //then
        assertThat(post.getContent()).isEqualTo(newContent);
    }

    @DisplayName("update 시 유효하지 않은 post id 사용하면 예외 발생")
    @Test
    public void whenPostUpdateWithInvalidPostId_thenThrowException() throws  Exception {
        //given
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.update(newContent, invalidPostId, memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_FOUND);
    }

    @DisplayName("update 시 유효하지 않은 member id 사용하면 예외 발생")
    @Test
    public void whenPostUpdateWithInvalidMemberId_thenThrowException() throws  Exception {
        //given
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.update(newContent, postId, invalidMemberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_WRITER);
    }

    @DisplayName("post create 시 유효하지 않은 member id 사용하면 예외 발생")
    @Test
    public void whenCreatePostWithNotExistMemberId_thenThrowException() throws  Exception {
        //given
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.create(title, content, invalidMemberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.ACCOUNT_NOT_FOUND);
    }
    
    @DisplayName("post 삭제 시 유효하지 않은 postId 사용하면 예외 발생")
    @Test
    public void whenDeletePostWithInvalidPostId_thenThrowsException() throws  Exception {
        //given
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.delete(invalidPostId, memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_FOUND);
    }

    @DisplayName("post 삭제 시 유효하지 않은 memberId 사용하면 예외 발생")
    @Test
    public void whenDeletePostWithInvalidMemberId_thenThrowsException() throws  Exception {
        //given
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.delete(postId, invalidMemberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_WRITER);
    }
}