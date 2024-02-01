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
import toy.board.controller.api.response.annotation.common.ApiFoundError;
import toy.board.controller.api.response.annotation.common.ApiPageError;
import toy.board.controller.api.response.annotation.member.ApiAuthenticationError;
import toy.board.controller.mypage.dto.response.MyCommentResponse;
import toy.board.controller.mypage.dto.response.MyInfoResponse;
import toy.board.controller.mypage.dto.response.MyPostResponse;
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
                            implementation = MyInfoResponse.class
                    )
            )
    )
    @ApiAuthenticationError
    @ApiFoundError
    @Operation(summary = "내 정보", description = "mypage에 사용되는 내 정보를 불러옵니다.")
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<MyInfoResponse> load(final HttpServletRequest request) {
        Long memberId = getMemberIdFrom(request);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BusinessException(ExceptionCode.NOT_FOUND)
                );

        return ResponseEntity.ok(
                MyInfoResponse.of(member)
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
    @ApiPageError
    @Operation(summary = "내가 작성한 게시물 목록 조회", description = "회원이 작성한 게시물 목록을 불러옵니다.")
    @GetMapping("/posts")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<MyPostResponse>> getPosts(
            @PageableDefault(
                    size = 5,
                    page = 0,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) final Pageable pageable,
            final HttpServletRequest request) {

        Long memberId = getMemberIdFrom(request);
        Page<Post> page = postRepository.findAllByWriterIdWithFetchWriter(memberId,
                pageable);
        return ResponseEntity.ok(
                page.map(MyPostResponse::of)
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
    @ApiPageError
    @Operation(summary = "내가 작성한 댓글 목록 조회", description = "회원이 작성한 댓글 목록을 불러옵니다.")
    @GetMapping("/comments")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<MyCommentResponse>> getComments(
            @PageableDefault(
                    size = 5,
                    page = 0,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) final Pageable pageable,
            final HttpServletRequest request) {

        Long memberId = getMemberIdFrom(request);
        Page<Comment> page = commentRepository
                .findAllNotDeletedCommentByWriterIdWithFetchJoinPostAndWriter(memberId,
                        pageable);

        return ResponseEntity.ok(
                page.map(MyCommentResponse::of)
        );

    }

    private Long getMemberIdFrom(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
