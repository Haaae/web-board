package toy.board.controller.mypage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.constant.SessionConst;
import toy.board.controller.api.response.annotation.ApiAuthenticationError;
import toy.board.controller.api.response.annotation.ApiFoundError;
import toy.board.controller.mypage.dto.MyCommentDto;
import toy.board.controller.mypage.dto.MyInfoDto;
import toy.board.controller.mypage.dto.MyPostDto;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.repository.user.MemberRepository;

@Tag(name = "MyPage", description = "MyPage API Document")
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @ApiResponse(
            responseCode = "200",
            description = "내 정보 불러오기 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = MyInfoDto.class
                    )
            )
    )
    @ApiAuthenticationError
    @ApiFoundError
    @Operation(summary = "내 정보", description = "mypage에 사용되는 내 정보를 불러옵니다.")
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<MyInfoDto> load(final HttpServletRequest request) {
        Long memberId = getMemberIdFrom(request);

        Member member = findMemberWithFetchJoinProfile(memberId);

        return ResponseEntity.ok(
                MyInfoDto.of(member)
        );
    }

    @ApiResponse(
            responseCode = "200",
            description = "내가 작성한 게시물 목록 불러오기 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = Page.class
                    )
            )
    )
    @ApiAuthenticationError
    @Operation(summary = "내가 작성한 게시물 목록 조회", description = "회원이 작성한 게시물 목록을 불러옵니다.")
    @GetMapping("/posts")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<MyPostDto>> getPosts(
            @PageableDefault(
                    size = 5,
                    page = 0,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) final Pageable pageable,
            final HttpServletRequest request) {

        Long memberId = getMemberIdFrom(request);
        Page<Post> page = postRepository.findAllByWriterIdFetchJoinWriterAndProfile(memberId,
                pageable);
        return ResponseEntity.ok(
                page.map(MyPostDto::of)
        );
    }

    @ApiResponse(
            responseCode = "200",
            description = "내가 작성한 댓글 목록 불러오기 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = Page.class
                    )
            )
    )
    @ApiAuthenticationError
    @Operation(summary = "내가 작성한 댓글 목록 조회", description = "회원이 작성한 댓글 목록을 불러옵니다.")
    @GetMapping("/comments")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<MyCommentDto>> getComments(
            @PageableDefault(
                    size = 5,
                    page = 0,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) final Pageable pageable,
            final HttpServletRequest request) {

        Long memberId = getMemberIdFrom(request);
        Page<Comment> page = commentRepository
                .findAllNotDeletedCommentByWriterIdWithFetchJoinPostAndWriterAndProfile(memberId,
                        pageable);

        return ResponseEntity.ok(
                page.map(MyCommentDto::of)
        );

    }

    private Long getMemberIdFrom(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    private Member findMemberWithFetchJoinProfile(long memberId) {
        return memberRepository.findMemberWithFetchJoinProfile(memberId)
                .orElseThrow(() ->
                        new BusinessException(ExceptionCode.NOT_FOUND)
                );
    }
}
