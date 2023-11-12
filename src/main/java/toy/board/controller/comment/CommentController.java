package toy.board.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.constant.SessionConst;
import toy.board.controller.api.response.annotation.ApiAuthenticationError;
import toy.board.controller.api.response.annotation.ApiAuthorityError;
import toy.board.controller.api.response.annotation.ApiBadRequestArgError;
import toy.board.controller.api.response.annotation.ApiCommentTypeError;
import toy.board.controller.api.response.annotation.ApiFoundError;
import toy.board.controller.api.response.annotation.ApiPostOfCommentError;
import toy.board.controller.comment.dto.reponse.CommentIdResponse;
import toy.board.controller.comment.dto.request.CommentCreationRequest;
import toy.board.controller.comment.dto.request.CommentUpdateRequest;
import toy.board.service.comment.CommentService;

@Tag(name = "Comment", description = "Comment API Document")
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentController {

    private final CommentService commentService;

    @ApiResponse(
            responseCode = "201",
            description = "댓글 생성 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = CommentIdResponse.class
                    )
            )
    )
    @ApiBadRequestArgError
    @ApiAuthenticationError
    @ApiCommentTypeError
    @ApiPostOfCommentError
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다.")
    @Parameter(name = "postId", description = "생성할 댓글이 소속된 게시물 Id")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentIdResponse> createComment(
            @RequestBody @Valid final CommentCreationRequest commentCreationRequest,
            @PathVariable("postId") final Long postId,
            final HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long commentId = commentService.create(
                commentCreationRequest.content(),
                commentCreationRequest.type(),
                Optional.ofNullable(commentCreationRequest.parentId()),
                postId,
                memberId
        );

        return new ResponseEntity<>(
                CommentIdResponse.of(commentId),
                HttpStatus.CREATED
        );
    }

    @ApiResponse(
            responseCode = "200",
            description = "댓글 수정 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = CommentIdResponse.class
                    )
            )
    )
    @ApiBadRequestArgError
    @ApiAuthorityError
    @ApiAuthenticationError
    @ApiFoundError
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @Parameter(name = "postId", description = "수정할 댓글이 소속된 게시물 Id")
    @Parameter(name = "commentId", description = "수정할 댓글 Id")
    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentIdResponse> updateComment(
            @PathVariable("commentId") final Long commentId,
            @RequestBody @Valid final CommentUpdateRequest commentUpdateDto,
            final HttpServletRequest request

    ) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long updatedCommentId = commentService.update(
                commentId, commentUpdateDto.content(), memberId
        );

        return ResponseEntity.ok(
                CommentIdResponse.of(updatedCommentId)
        );
    }

    @ApiResponse(
            responseCode = "200",
            description = "댓글 삭제 성공"
    )
    @ApiBadRequestArgError
    @ApiAuthenticationError
    @ApiAuthorityError
    @ApiFoundError
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @Parameter(name = "postId", description = "삭제할 댓글이 소속된 게시물 Id")
    @Parameter(name = "commentId", description = "삭제할 댓글 Id")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity deleteComment(
            @PathVariable("commentId") final Long commentId,
            final HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
        commentService.delete(commentId, memberId);

        return new ResponseEntity(HttpStatus.OK);
    }
}
