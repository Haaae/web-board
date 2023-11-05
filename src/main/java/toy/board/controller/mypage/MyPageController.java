package toy.board.controller.mypage;

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
import toy.board.controller.mypage.dto.MyCommentDto;
import toy.board.controller.mypage.dto.MyInfoDto;
import toy.board.controller.mypage.dto.MyPageDto;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.repository.user.MemberRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<MyInfoDto> load(final HttpServletRequest request) {
        Long memberId = getMemberIdFrom(request);

        Member member = getMemberWithProfile(memberId);

        return ResponseEntity.ok(
                MyInfoDto.of(member)
        );
    }

    @GetMapping("/posts")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<MyPageDto>> getPosts(
            @PageableDefault(
                    size = 5,
                    page = 0,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) final Pageable pageable,
            final HttpServletRequest request) {

        Long memberId = getMemberIdFrom(request);
        Page<Post> page = postRepository.findAllByWriterIdFetchJoinWriterAndProfile(memberId, pageable);
        return ResponseEntity.ok(
                page.map(MyPageDto::of)
        );
    }

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
                .findAllNotDeletedCommentByWriterIdWithFetchJoinPostAndWriterAndProfile(memberId, pageable);

        return ResponseEntity.ok(
                page.map(MyCommentDto::of)
        );

    }

    private static Long getMemberIdFrom(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

    private Member getMemberWithProfile(long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() ->
                        new BusinessException(ExceptionCode.ACCOUNT_NOT_FOUND)
                );
    }
}
