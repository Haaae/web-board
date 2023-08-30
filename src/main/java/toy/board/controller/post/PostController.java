package toy.board.controller.post;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import toy.board.constant.SessionConst;
import toy.board.controller.post.dto.*;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.comment.CommentRepository;
import toy.board.repository.post.PostRepository;
import toy.board.service.CommentService;
import toy.board.service.PostService;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // read

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<PostDto>> getPosts(final Pageable pageable) {
        Page<PostDto> page = postRepository.findAllPost(pageable);

        return ResponseEntity.ok(page);
    }

//    @GetMapping("/comments")
//    public ResponseEntity<List<CommentDto>> getComments() {
//        List<CommentDto> comments = commentService.getComments();
//        return ResponseEntity.ok(comments);
//    } === 필요한가? ===

    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPostDetail(@PathVariable("postId") final Long postId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("postDto",
                postRepository.getPostDtoById(postId).orElseThrow(
                        () -> new BusinessException(ExceptionCode.POST_NOT_FOUND)
                ));
        map.put("commentDtos", commentRepository.getCommentDtosByPostId(postId));
        return ResponseEntity.ok(map);
    }

    // createComment

    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestBody final PostCreationRequest postCreationRequest,
            final HttpServletRequest request
    ) {

        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
        Long postId = postService.create(
                postCreationRequest.title(),
                postCreationRequest.content(),
                memberId
        );

        return new ResponseEntity<>(postId, HttpStatus.CREATED);
        // 이렇게 dto말고 long 객체로 보내면 어케 되는지 확인
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Long> createComment(
            @RequestBody final CommentCreationRequest commentCreationRequest,
            @PathVariable("postId") final Long postId,
            final HttpServletRequest request
    ) {
        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
        Long commentId = commentService.create(
                commentCreationRequest.content(),
                commentCreationRequest.type(),
                commentCreationRequest.parentId(),
                postId,
                memberId
        );

        return new ResponseEntity<>(commentId, HttpStatus.CREATED);
    }

    // update

    @PatchMapping("/{postId}")
    public ResponseEntity<Long> updatePost(
            @RequestBody final PostUpdateDto postUpdateDto,
            @PathVariable("postId") final Long postId,
            final HttpServletRequest request
    ) {
        // TODO: 2023-08-30 작성자와 다른 memberId 수정 안되는지 확인
        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
        Long updatedPostId = postService.update(
                postUpdateDto.content(),
                postId,
                memberId
        );

        return ResponseEntity.ok(updatedPostId);
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Long> updateComment(
            @PathVariable("commentId") final Long commentId,
            @RequestBody final CommentUpdateDto commentUpdateDto,
            final HttpServletRequest request

    ) {
        // TODO: 2023-08-30 작성자와 다른 memberId 수정 안되는지 확인
        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
        Long updatedCommentId = commentService.update(
                commentId, commentUpdateDto.content(), memberId
        );

        return ResponseEntity.ok(updatedCommentId);
    }

    // delete

    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(
            @PathVariable("postId") final Long postId,
            final HttpServletRequest request
    ) {
        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
        postService.delete(postId, memberId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity deleteComment(
            @PathVariable("commentId") final Long commentId,
            final HttpServletRequest request
    ) {
        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
        commentService.delete(commentId, memberId);

        return new ResponseEntity(HttpStatus.OK);
    }

/*
myposts, mycomment 같이 mypage에 필요한 데이터는 MyController에서 한 번에 전달하는 것이 좋지 않을까?
 */
//    @GetMapping("/myposts")
//    public ResponseEntity getMyPosts(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        Object memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
//        postService.find
//
//
//        return null;
//    }

//    @GetMapping("/mycomments")
//    public ResponseEntity getMyComments() {
//        return null;
//    }
}
