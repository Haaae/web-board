package toy.board.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.service.post.PostService;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private EntityManager em;

    private Long memberId;
    private String content = "content";
    private String title = "title";

    Long invalidPostId = 1242L;
    Long invalidMemberId = 23523L;

    @BeforeEach
    void init() {
        Member member = MemberTest.create();
        em.persist(member);
        memberId = member.getId();
        em.flush(); em.clear();
    }

    @DisplayName("update 성공 시 update post id 반환")
    @Test
    public void whenPostUpdate_thenReturnValidValue() throws  Exception {
        //given
        Long postId = postService.create(title, content, memberId);
        //when
        //then
        Long updatedPostId = postService.update(content, postId, memberId);
        assertThat(updatedPostId).isEqualTo(postId);
    }

    @DisplayName("update 시 유효하지 않은 post id 사용하면 예외 발생")
    @Test
    public void whenPostUpdateWithInvalidPostId_thenThrowException() throws  Exception {
        //given
        Long postId = postService.create(title, content, memberId);
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.update(content, invalidPostId, memberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_FOUND);
    }

    @DisplayName("update 시 유효하지 않은 member id 사용하면 예외 발생")
    @Test
    public void whenPostUpdateWithInvalidMemberId_thenThrowException() throws  Exception {
        //given
        Long postId = postService.create(title, content, memberId);
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.update(content, postId, invalidMemberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_WRITER);
    }

    @DisplayName("post create의 반환값이 정상적으로 반환됨")
    @Test
    public void whenCreatePost_thenReturnValidValue() throws Exception {
        //given
        Long postId = postService.create(title, content, memberId);
        //when
        Post post = em.find(Post.class, postId);
        //then
        System.out.println("postId = " + postId);
        assertThat(postId).isNotNull();
        assertThat(postId).isEqualTo(post.getId());
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

    @DisplayName("post 정상 삭제")
    @Test
    public void PostServiceTest() throws  Exception {
        //given
        Long postId = postService.create(title, content, memberId);
        //when
        postService.delete(postId, memberId);
        //then
        assertThat(em.find(Post.class, postId)).isNull();
    }
    
    @DisplayName("post 삭제 시 유효하지 않은 postId 사용하면 예외 발생")
    @Test
    public void whenDeletePostWithInvalidPostId_thenThrowsException() throws  Exception {
        //given
        Long postId = postService.create(title, content, memberId);
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
        Long postId = postService.create(title, content, memberId);
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.delete(postId, invalidMemberId));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.POST_NOT_WRITER);
    }
}