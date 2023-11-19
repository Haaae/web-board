package toy.board.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.constant.SessionConst;
import toy.board.controller.api.response.annotation.common.ApiAuthorityError;
import toy.board.controller.api.response.annotation.common.ApiBadRequestArgError;
import toy.board.controller.api.response.annotation.common.ApiFoundError;
import toy.board.controller.api.response.annotation.common.ApiPageError;
import toy.board.controller.api.response.annotation.member.ApiAuthenticationError;
import toy.board.controller.post.dto.reponse.PostIdResponse;
import toy.board.controller.post.dto.request.PostCreationRequest;
import toy.board.controller.post.dto.request.PostUpdateRequest;
import toy.board.domain.post.Post;
import toy.board.repository.post.PostRepository;
import toy.board.service.post.PostService;
import toy.board.service.post.dto.PostDetailResponse;
import toy.board.service.post.dto.PostResponse;

@Tag(name = "Post", description = "Post API Document")
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    /*
     * size == 0 or (page and size < (0 or String)) -> 400 error
     *
    **요청 파라미터**
        - 예) `/members?page=0&size=3&sort=id,desc&sort=username, desc`
        - `page`: 현재 페이지, 0부터 시작한다.
        - `size`: 한 페이지에 노출할 데이터 건수
        - `sort`: 정렬 조건을 정의한다. 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort 파라미터 추가 (`asc` 생략 가능)
     */
    @ApiResponse(
            responseCode = "200",
            description = "모든 게시물 목록 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = Page.class
                    )
            )
    )
    @ApiPageError
    @Operation(summary = "모든 게시물 목록 조회", description = "모든 게시물을 페이징하여 조회합니다.")
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<PostResponse>> getPosts(
            @PageableDefault(
                    size = 5,
                    page = 0,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Post> page = postRepository.findAllWithFetchJoinWriterAndProfile(pageable);
        return ResponseEntity.ok(
                page.map(PostResponse::of)
        );
    }

    @ApiResponse(
            responseCode = "200",
            description = "게시물 상세 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = PostDetailResponse.class
                    )
            )
    )
    @Operation(summary = "게시물 상세 조회", description = "게시물 상세 정보를 조회합니다.")
    @Parameter(name = "postId", description = "조회할 게시물 Id")
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable("postId") final Long postId) {
        PostDetailResponse postDetail = postService.getPostDetail(postId);
        return ResponseEntity.ok(postDetail);
    }

    @ApiResponse(
            responseCode = "201",
            description = "게시물 생성 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = PostIdResponse.class
                    )
            )
    )
    @ApiBadRequestArgError
    @ApiAuthenticationError
    @Operation(summary = "게시물 생성", description = "게시물을 생성합니다.")
    @PostMapping
    public ResponseEntity<PostIdResponse> createPost(
            @RequestBody @Valid final PostCreationRequest postCreationRequest,
            final HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long postId = postService.create(
                postCreationRequest.title(),
                postCreationRequest.content(),
                memberId
        );

        return new ResponseEntity<>(
                PostIdResponse.from(postId),
                HttpStatus.CREATED
        );
    }

    // update

    @ApiResponse(
            responseCode = "200",
            description = "게시물 수정 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = PostIdResponse.class
                    )
            )
    )
    @ApiBadRequestArgError
    @ApiAuthenticationError
    @ApiAuthorityError
    @ApiFoundError
    @Operation(summary = "게시물 수정", description = "게시물을 수정합니다.")
    @Parameter(name = "postId", description = "수정할 게시물 Id")
    @PatchMapping("/{postId}")
    public ResponseEntity<PostIdResponse> updatePost(
            @RequestBody @Valid final PostUpdateRequest postUpdateDto,
            @PathVariable("postId") final Long postId,
            final HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long updatedPostId = postService.update(
                postUpdateDto.content(),
                postId,
                memberId
        );

        return ResponseEntity.ok(
                PostIdResponse.from(updatedPostId)
        );
    }


    @ApiResponse(
            responseCode = "200",
            description = "게시물 삭제 성공"
    )
    @ApiBadRequestArgError
    @ApiAuthenticationError
    @ApiAuthorityError
    @ApiFoundError
    @Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다.")
    @Parameter(name = "postId", description = "삭제할 게시물 Id")
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(
            @PathVariable("postId") final Long postId,
            final HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
        postService.delete(postId, memberId);

        return new ResponseEntity(HttpStatus.OK);
    }
}
