package toy.board.controller.post;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.controller.post.dto.PostListDto;
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

    // read

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<PostListDto>> getPosts(Pageable pageable) {
        Page<PostListDto> page = postRepository.findAllPost(pageable);

        return ResponseEntity.ok(page);
    }

//    @GetMapping("/comments")
//    public ResponseEntity<List<CommentDto>> getComments() {
//        List<CommentDto> comments = commentService.getComments();
//        return ResponseEntity.ok(comments);
//    } === 필요한가? ===

//    @GetMapping("/{postId}")
//    public ResponseEntity<PostDetailDto> getPostDetail(@PathParam("postId") Long postId) {
//        PostDetailDto postDetail = postService.getPostDetail(postId);
//        return ResponseEntity.ok(postDetail);
//    }
//
//    // create
//
//    @PostMapping
//    public ResponseEntity<Long> createPost(
//            @RequestBody PostCreationRequest postCreationRequest,
//            HttpServletRequest request
//    ) {
//
//        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
//        Long postId = postService.create(
//                postCreationRequest.title(),
//                postCreationRequest.content(),
//                memberId
//        );
//
//        return new ResponseEntity<>(postId, HttpStatus.CREATED);
//        // 이렇게 dto말고 long 객체로 보내면 어케 되는지 확인
//    }
//
//    @PostMapping("/{postId}/comments")
//    public ResponseEntity createComment(
//            @RequestBody CommentCreationRequest commentCreationRequest,
//            HttpServletRequest request
//    ) {
//        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
//        Long commentId = commentService.create(
//                commentCreationRequest.content(),
//                commentCreationRequest.type(),
//                commentCreationRequest.bundleId(),
//                memberId
//        );
//
//        return new ResponseEntity<>(commentId, HttpStatus.CREATED);
//    }
//
//    // update
//
//    @PatchMapping("/{postId}")
//    public ResponseEntity updatePost(
//            @RequestBody PostUpdateDto postUpdateDto,
//            @PathParam("postId") Long postId,
//            HttpServletRequest request
//    ) {
//
//        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        PostUpdateCommand postUpdateCommand = new PostUpdateCommand(
//                postUpdateDto.content(),
//                postId,
//                memberId    // 필요한가? 필요하다. 세션의 멤버와 작성자를 대조해봐야 함
//        );
//
//        Long updatedPostId = PostService.update(postUpdateCommand);
//
//        return ResponseEntity.ok(updatedPostId);
//    }
//
//    @PatchMapping("/{postId}/comments/{commentId}")
//    public ResponseEntity updateComment(
////            @PathParam("postId") Long postId,
//            @PathParam("commentId") Long commentId,
//            @RequestBody CommentUpdateDto commentUpdateDto,
//            HttpServletRequest request
//
//    ) {
//
//        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        Long updatedCommentId = commentService.update(
//                commentId, commentUpdateDto.content(), memberId
//        );
//
//        return ResponseEntity.ok(updatedCommentId);
//    }
//
//    // delete
//
//    @DeleteMapping("/{postId}")
//    public ResponseEntity deletePost(
//            @PathParam("postId") Long postId,
//            HttpServletRequest request
//    ) {
//
//        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        postService.delete(postId, memberId);
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{postId}/comments/{commentId}")
//    public ResponseEntity deleteComment(
////            @PathParam("postId") Long postId,
//            @PathParam("commentId") Long commentId,
//            HttpServletRequest request
//    ) {
//
//        Long memberId = (Long) request.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        commentService.delete(commentId, memberId);
//
//        return new ResponseEntity(HttpStatus.OK);
//    }

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
