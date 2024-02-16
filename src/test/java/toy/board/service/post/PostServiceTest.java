package toy.board.service.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentTest;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.repository.user.MemberRepository;
import toy.board.service.post.dto.CommentResponse;
import toy.board.service.post.dto.CommentsResponse;
import toy.board.service.post.dto.PostDetailResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CommentRepository commentRepository;

    private final Random random = new Random();


    @Nested
    class CreateTest {

        @DisplayName("수정 성공")
        @Test
        public void 수정_성공() throws Exception {
            //given
            String title = "title";
            String content = "content";

            Long memberId = random.nextLong();
            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            //when
            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));
            given(postRepository.save(any()))
                    .willReturn(post);

            //then
            assertDoesNotThrow(
                    () -> postService.create(title, content, memberId)
            );
        }

        @DisplayName("생성 실패 : 게시물 생성시 유효하지 않은 member id 사용하면 예외 발생")
        @Test
        public void 게시물_생성시_작성자_id를_찾지_못하연_예외발생() throws Exception {
            //given
            String title = "title";
            String content = "content";

            Long notExistMemberId = random.nextLong();

            //when
            given(memberRepository.findById(eq(notExistMemberId)))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.create(title, content, notExistMemberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }
    }

    @Nested
    class UpdateTest {

        @DisplayName("수정 성공")
        @Test
        public void 수정_성공() throws Exception {
            //given
            String newContent = "new content";

            Long postId = random.nextLong();
            Long memberId = random.nextLong();
            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            //when
            given(postRepository.findPostWithFetchJoinWriter(eq(postId)))
                    .willReturn(Optional.of(post));

            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));

            //then
            assertDoesNotThrow(
                    () -> postService.update(newContent, postId, memberId)
            );

            assertThat(post.getContent()).isEqualTo(newContent);
        }

        @DisplayName("수정 실패 : post id에 해당하는 게시물을 찾을 수 없다면 예외 발생")
        @Test
        public void 수정시_게시물을_찾지_못하면_예외발생() throws Exception {
            //given
            String newContent = "new content";

            Long notExistPostId = random.nextLong();
            Long memberId = random.nextLong();
            Member member = MemberTest.create();

            //when
            given(postRepository.findPostWithFetchJoinWriter(eq(notExistPostId)))
                    .willReturn(Optional.empty());

            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.update(newContent, notExistPostId, memberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("수정 실패 : 수정시 member id에 해당하는 사용자를 찾을 수 없다면 예외 발생")
        @Test
        public void 수정시_작성자를_찾지_못하면_예외발생() throws Exception {
            //given
            String newContent = "new content";

            Long postId = random.nextLong();
            Long invalidMemberId = random.nextLong();

            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            //when
            given(postRepository.findPostWithFetchJoinWriter(eq(postId)))
                    .willReturn(Optional.of(post));
            given(memberRepository.findById(eq(invalidMemberId)))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.update(newContent, postId, invalidMemberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("수정 실패 : 수정시 권한없는 member id를 사용하면 예외 발생")
        @Test
        public void 수정시_권한없는_작성자_id라면_예외발생() throws Exception {
            //given
            String newContent = "new content";

            Long postId = random.nextLong();
            Long invalidMemberId = random.nextLong();

            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            //when
            Member invalidMember = MemberTest.create();

            given(postRepository.findPostWithFetchJoinWriter(eq(postId)))
                    .willReturn(Optional.of(post));

            given(memberRepository.findById(eq(invalidMemberId)))
                    .willReturn(Optional.of(invalidMember));

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.update(newContent, postId, invalidMemberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.INVALID_AUTHORITY);
        }
    }

    @Nested
    class DeleteTest {

        @DisplayName("삭제 성공")
        @Test
        public void 삭제_성공() throws Exception {
            //given
            Long postId = random.nextLong();
            Long memberId = random.nextLong();
            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            //when
            given(postRepository.findPostWithFetchJoinWriter(eq(postId)))
                    .willReturn(Optional.of(post));

            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));

            //then
            assertDoesNotThrow(
                    () -> postService.delete(postId, memberId)
            );
        }

        @DisplayName("삭제 실패 : 삭제시 게시물을 찾지 못하면 예외 발생")
        @Test
        public void 삭제시_게시물을_찾지_못하면_예외발생() throws Exception {
            //given
            Long notExistPostId = random.nextLong();
            Long memberId = random.nextLong();
            Member member = MemberTest.create();

            //when
            given(postRepository.findPostWithFetchJoinWriter(eq(notExistPostId)))
                    .willReturn(Optional.empty());

            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.delete(notExistPostId, memberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("삭제 실패 : 삭제시 member id에 해당하는 사용자를 찾을 수 없다면 예외 발생")
        @Test
        public void 삭제시_작성자를_찾지_못하면_예외발생() throws Exception {
            //given
            Long postId = random.nextLong();
            Long invalidMemberId = random.nextLong();

            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            //when
            given(postRepository.findPostWithFetchJoinWriter(eq(postId)))
                    .willReturn(Optional.of(post));
            given(memberRepository.findById(eq(invalidMemberId)))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.delete(postId, invalidMemberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("삭제 실패 : 삭제시 권한없는 member id를 사용하면 예외 발생")
        @Test
        public void 삭제시_권한없는_작성자_id라면_예외발생() throws Exception {
            //given
            Long postId = random.nextLong();
            Long invalidMemberId = random.nextLong();

            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            //when
            Member invalidMember = MemberTest.create();

            given(postRepository.findPostWithFetchJoinWriter(eq(postId)))
                    .willReturn(Optional.of(post));

            given(memberRepository.findById(eq(invalidMemberId)))
                    .willReturn(Optional.of(invalidMember));

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.delete(postId, invalidMemberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.INVALID_AUTHORITY);
        }

    }

    @Nested
    class FetchTest {
        @DisplayName("게시물 조회")
        @Test
        public void 게시물_조회_성공() throws Exception {
            //given
            Long postId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);
            Comment comment = CommentTest.create(post, CommentType.COMMENT);

            int countOfComment = 1;
            int countOfReply = 3;

            for (int i = 0; i < countOfReply; i++) {
                CommentTest.create(post, CommentType.REPLY, comment);
            }

            //when
            given(postRepository.findPostWithFetchJoinWriterAndComments(eq(postId)))
                    .willReturn(Optional.of(post));

            PostDetailResponse postDetail = postService.getPostDetail(postId);
            List<CommentResponse> comments = postDetail.comments().comments();
            CommentsResponse replies = comments.get(0).replies();

            Long expectedHits = 1L;

            //then
            assertThat(comments.size()).isEqualTo(countOfComment);
            assertThat(replies.count()).isEqualTo(countOfReply);
            assertThat(post.getHits()).isEqualTo(expectedHits);
        }

        @DisplayName("게시물 조회 실패 : 게시물을 찾지 못하면 예외 발생")
        @Test
        void 게시물을_찾지_못하면_예외_발생() throws Exception {
            //given
            Long notExistPostId = random.nextLong();

            //when
            given(postRepository.findPostWithFetchJoinWriter(eq(notExistPostId)))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> postService.getPostDetail(notExistPostId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }
    }
}
