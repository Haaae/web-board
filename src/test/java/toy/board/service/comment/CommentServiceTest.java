package toy.board.service.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;

    private final Random random = new Random();

    @Nested
    class CreateTest {
        private String content = "content";

        @DisplayName("Comment 타입 댓글 생성 성공")
        @Test
        public void Comment_타입의_댓글_생성() throws Exception {
            //given
            Optional<Long> parentId = Optional.empty();
            Long postId = random.nextLong();
            Long memberId = random.nextLong();

            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            given(memberRepository.findById(anyLong()))
                    .willReturn(
                            Optional.of(member)
                    );
            given(postRepository.findPostWithFetchJoinWriter(anyLong()))
                    .willReturn(
                            Optional.of(post)
                    );
            given(commentRepository.findCommentWithFetchJoinWriter(null))
                    .willReturn(Optional.empty());

            //when
            //then
            assertDoesNotThrow(() ->
                    commentService.create(content, CommentType.COMMENT, parentId, postId, memberId)
            );
        }

        @DisplayName("Reply 타입 댓글 생성 성공")
        @Test
        public void Reply_타입의_댓글_생성() throws Exception {
            //given
            Long memberId = random.nextLong();
            Long postId = random.nextLong();
            Long parentCommentId = random.nextLong();
            Optional<Long> parentId = Optional.of(parentCommentId);

            Member member = MemberTest.create();
            Post post = PostTest.create(member);
            Comment parentComment = CommentTest.create(post, CommentType.COMMENT);

            given(memberRepository.findById(memberId))
                    .willReturn(
                            Optional.of(member)
                    );
            given(postRepository.findPostWithFetchJoinWriter(postId))
                    .willReturn(
                            Optional.of(post)
                    );
            given(commentRepository.findCommentWithFetchJoinWriter(parentCommentId))
                    .willReturn(Optional.of(parentComment));

            //when
            //then
            assertDoesNotThrow(() ->
                    commentService.create(content, CommentType.REPLY, parentId, postId, memberId)
            );
        }

        @DisplayName("생성 실패: post id에 맞는 post가 존재하지 않음")
        @Test
        public void 게시물이_없다면_예외() throws Exception {
            //given
            Long memberId = random.nextLong();
            Optional<Long> parentId = Optional.empty();

            Member member = MemberTest.create();

            given(memberRepository.findById(memberId))
                    .willReturn(
                            Optional.of(member)
                    );
            given(postRepository.findPostWithFetchJoinWriter(anyLong()))
                    .willReturn(
                            Optional.empty()    // 댓글이 소속될 게시물을 찾지 못한 경우
                    );
            given(commentRepository.findCommentWithFetchJoinWriter(anyLong()))
                    .willReturn(Optional.empty());

            //when
            Long invalidPostId = 12132L;

            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.create(content, CommentType.COMMENT, parentId, invalidPostId,
                            memberId)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("생성 실패: memberId에 맞는 회원이 존재하지 않음")
        @Test
        public void 회원이_없다면_예외() throws Exception {
            //given
            Long postId = random.nextLong();
            Long memberId = random.nextLong();
            Optional<Long> parentId = Optional.empty();

            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            given(memberRepository.findById(anyLong()))
                    .willReturn(
                            Optional.empty()    // 작성자에 해당하는 member를 찾지 못한 경우
                    );
            given(postRepository.findPostWithFetchJoinWriter(postId))
                    .willReturn(
                            Optional.of(post)
                    );
            given(commentRepository.findCommentWithFetchJoinWriter(anyLong()))
                    .willReturn(Optional.empty());

            //when
            Long invalidMemberId = 12132L;

            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.create(content, CommentType.COMMENT, parentId, postId,
                            invalidMemberId)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("생성 실패: 대댓글 생성 시 주어진 CommentId에 해당하는 Comment가 존재하지 않음")
        @Test
        public void 부모_댓글을_찾지_못하면_예외() throws Exception {
            //given
            Long memberId = random.nextLong();
            Long postId = random.nextLong();
            Long parentCommentId = random.nextLong();
            Optional<Long> parentId = Optional.of(parentCommentId);

            Member member = MemberTest.create();
            Post post = PostTest.create(member);

            given(memberRepository.findById(memberId))
                    .willReturn(
                            Optional.of(member)
                    );

            given(postRepository.findPostWithFetchJoinWriter(postId))
                    .willReturn(
                            Optional.of(post)
                    );

            given(commentRepository.findCommentWithFetchJoinWriter(parentCommentId))
                    .willReturn(
                            Optional.empty()
                    );  // 부모 댓글을 찾지 못한 경우

            //when
            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.create(content, CommentType.REPLY, parentId, postId,
                            memberId)
            );

            // Comment Service에서 repository를 통해 parent comment를 찾아오는 과정에서는 단지 빈 Optional을 반환한다.
            // 이후 Comment를 생성하는 과정에서 에러가 발생한다.
            assertThat(e.getCode()).isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);
        }

        @DisplayName("생성 실패: 대댓글 생성 시 주어진 CommentId가 null인 경우")
        @Test
        public void 주어진_부모_댓글의_id가_null이면_예외() throws Exception {
            //given
            Long memberId = random.nextLong();
            Long postId = random.nextLong();
            Long parentCommentId = random.nextLong();
            Optional<Long> parentId = Optional.empty();

            Member member = MemberTest.create();
            Post post = PostTest.create(member);
            Comment parentComment = CommentTest.create(post, CommentType.COMMENT);

            given(memberRepository.findById(memberId))
                    .willReturn(
                            Optional.of(member)
                    );
            given(postRepository.findPostWithFetchJoinWriter(postId))
                    .willReturn(
                            Optional.of(post)
                    );
            given(commentRepository.findCommentWithFetchJoinWriter(parentCommentId))
                    .willReturn(Optional.of(parentComment));  // 부모 댓글을 찾지 못한 경우

            //when
            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.create(content, CommentType.REPLY, parentId, postId,
                            memberId)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);  // Comment 생성시 에러 발생
        }
    }

    @Nested
    class UpdateTest {
        // update
        @DisplayName("수정 성공")
        @Test
        public void 수정_성공() throws Exception {
            //given
            String updateContent = "update comment";

            Long memberId = random.nextLong();
            Long commentId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);
            Comment comment = CommentTest.create(post, CommentType.COMMENT);

            given(commentRepository.findCommentWithFetchJoinWriter(commentId))
                    .willReturn(Optional.of(comment));
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(writer));

            //when

            //then
            assertDoesNotThrow(() -> commentService.update(commentId, updateContent, memberId));
            assertThat(comment.getContent()).isEqualTo(updateContent);
        }

        @DisplayName("수정 실패: 수정 권한 없는 사용자")
        @Test
        public void 수정권환없는_사용자라면_예외() throws Exception {
            //given
            String updateContent = "update comment";

            Long invalidMemberId = random.nextLong();
            Long commentId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);
            Comment comment = CommentTest.create(post, CommentType.COMMENT);

            given(commentRepository.findCommentWithFetchJoinWriter(commentId))
                    .willReturn(Optional.of(comment));

            //when
            Member other = MemberTest.create();
            given(memberRepository.findById(invalidMemberId))
                    .willReturn(Optional.of(other));

            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.update(commentId, updateContent, invalidMemberId)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.INVALID_AUTHORITY);
        }

        @DisplayName("수정 실패: commentId에 해당하는 comment가 없음")
        @Test
        public void 댓글이_없으면_예외() throws Exception {
            //given
            String updateContent = "update comment";

            Long memberId = random.nextLong();
            Long invalidCommentId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);

            given(commentRepository.findCommentWithFetchJoinWriter(invalidCommentId))
                    .willReturn(Optional.empty());
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(writer));

            //when
            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.update(invalidCommentId, updateContent, memberId));
            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("수정 실패: memberId에 해당하는 member가 없음")
        @Test
        public void 작성자가_없으면_예외() throws Exception {
            //given
            String updateContent = "update comment";

            Long memberId = random.nextLong();
            Long commentId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);
            Comment comment = CommentTest.create(post, CommentType.COMMENT);

            given(commentRepository.findCommentWithFetchJoinWriter(commentId))
                    .willReturn(Optional.of(comment));
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            //when
            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.update(commentId, updateContent, memberId));
            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }
    }

    @Nested
    class DeleteTest {
        @DisplayName("삭제 성공")
        @Test
        public void 삭제_성공() throws Exception {

            //given
            Long memberId = random.nextLong();
            Long commentId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);
            Comment comment = CommentTest.create(post, CommentType.COMMENT);

            given(commentRepository.findCommentWithFetchJoinWriter(commentId))
                    .willReturn(Optional.of(comment));
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(writer));

            //when
            //then
            assertDoesNotThrow(() -> commentService.delete(commentId, memberId));

            commentService.delete(commentId, memberId);
            assertThat(comment.isDeleted()).isTrue();
        }

        @DisplayName("삭제 실패: 삭제 권한 없는 사용자")
        @Test
        public void 사용자가_삭제권한이_없다면_예외() throws Exception {
            //given
            Long invalidMemberId = random.nextLong();
            Long commentId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);
            Comment comment = CommentTest.create(post, CommentType.COMMENT);

            given(commentRepository.findCommentWithFetchJoinWriter(commentId))
                    .willReturn(Optional.of(comment));

            //when
            Member invalidMember = MemberTest.create();
            given(memberRepository.findById(invalidMemberId))
                    .willReturn(Optional.of(invalidMember));

            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> commentService.delete(commentId, invalidMemberId)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.INVALID_AUTHORITY);
        }

        @DisplayName("삭제 실패: commentId에 해당하는 comment가 없음")
        @Test
        public void 댓글이_없다면_예외() throws Exception {
            //given
            Long memberId = random.nextLong();
            Long commentId = random.nextLong();

            Member writer = MemberTest.create();

            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(writer));

            //when
            given(commentRepository.findCommentWithFetchJoinWriter(commentId))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> commentService.delete(commentId, memberId)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("삭제 실패: memberId에 해당하는 member가 없음")
        @Test
        public void 작성자가_없다면_예외() throws Exception {
            //given
            Long memberId = random.nextLong();
            Long commentId = random.nextLong();

            Member writer = MemberTest.create();
            Post post = PostTest.create(writer);
            Comment comment = CommentTest.create(post, CommentType.COMMENT);

            given(commentRepository.findCommentWithFetchJoinWriter(commentId))
                    .willReturn(Optional.of(comment));

            //when
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> commentService.delete(commentId, memberId)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }
    }

}