package toy.board.repository.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.UserRole;
import toy.board.service.post.dto.PostResponse;

@DataJpaTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private PostRepository postRepository;

    @DisplayName("memberId와 일치하는 writerId를 갖는 Post 페이징 : 정상동작")
    @Test
    public void 사용자의_작성게시물_페이징_정상동작() throws Exception {
        //given
        long memberId = insertMemberAndPost(10, 2);

        int pageNum = 0;
        int size = 10;
        int numberOfElements = 2;
        int totalPages = 1;
        int totalElements = 2;
        String sort = "createdDate";

        PageRequest pageable = PageRequest.of(pageNum, size, Sort.by(sort));

        //when
        Page<Post> page = postRepository.findAllByWriterIdWithFetchWriter(memberId,
                pageable);

        //then
        assertThat(page.getNumber()).isEqualTo(pageNum);
        assertThat(page.getNumberOfElements()).isEqualTo(numberOfElements);
        assertThat(page.getTotalPages()).isEqualTo(totalPages);
        assertThat(page.getTotalElements()).isEqualTo(totalElements);
        assertThat(page.getSize()).isEqualTo(size);
        assertThat(page.hasNext()).isFalse();
        assertThat(page.isFirst()).isTrue();
    }

    @DisplayName("정상동작 : 탈퇴한 회원의 post 가져오기")
    @Test
    public void 탈퇴한_회원_게시물_가져오기() throws Exception {
        //given
        Post post = PostTest.create("random", "random");
        Comment comment = new Comment(post, post.getWriter(), "content", CommentType.COMMENT, null);

        em.persist(post.getWriter());
        em.persist(post);
        em.persist(comment);
        Long postId = post.getId();

        post.getWriter().changeAllPostAndCommentWriterToNull(); // 회원 탈퇴 적용. 작성한 모든 게시물과 댓글 작성자를 null로 변경
        em.flush();
        em.clear();

        //when
        Optional<Post> findPost = postRepository.findPostWithFetchJoinWriterAndComments(
                postId);

        //then
        assertThat(findPost.isPresent()).isTrue();
        Post findPostGet = findPost.get();

        assertThat(findPostGet.getComments()).isNotNull();

        assertThatNoException()
                .isThrownBy(() ->
                        PostResponse.of(findPostGet)
                );
    }

    @DisplayName("정상동작 : findAll")
    @Test
    public void whenFindAllPostUsingSpringDataJpa_thenSuccess() throws Exception {
        //given
        insertMemberAndPost(10, 2);

        int pageNum = 1;
        int size = 10;
        int totalPages = 2;
        int totalElements = 20;
        String sort = "createdDate";

        PageRequest pageable = PageRequest.of(pageNum, size, Sort.by(sort));
        //when

        Page<Post> page = postRepository.findAllWithFetchJoinWriter(pageable);

        //then
        assertThat(page.getNumber()).isEqualTo(pageNum);
        assertThat(page.getNumberOfElements()).isEqualTo(size);
        assertThat(page.getTotalPages()).isEqualTo(totalPages);
        assertThat(page.getTotalElements()).isEqualTo(totalElements);
        assertThat(page.getSize()).isEqualTo(size);
        assertThat(page.hasNext()).isFalse();
        assertThat(page.isFirst()).isFalse();
    }

    private Long insertMemberAndPost(int memberNum, int postNum) {
        Long memberId = -1L;

        for (int i = 0; i < memberNum; i++) {
            Member member = persistMember(i);
            memberId = member.getId();

            for (int j = 0; j < postNum; j++) {
                Post post = persistPost(member, j);
                persistCommentAndReply(member, post);
            }
        }

        em.flush();
        em.clear();

        return memberId;
    }

    private void persistCommentAndReply(Member member, Post post) {
        Comment comment = new Comment(post, member, "title", CommentType.COMMENT, null);
        em.persist(comment);
        Comment reply = new Comment(post, member, "title", CommentType.REPLY, comment);
        em.persist(reply);
    }

    private Post persistPost(Member member, int index) {
        Post post = new Post(
                member,
                "title" + index,
                "content"
        );

        em.persist(post);
        return post;
    }

    private Member persistMember(int index) {
        Member member = new Member(
                "email" + index,
                "nick" + index,
                "password",
                UserRole.USER
        );

        em.persist(member);
        return member;
    }
}