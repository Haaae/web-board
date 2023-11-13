package toy.board.repository.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static toy.board.domain.post.QComment.comment;
import static toy.board.domain.post.QPost.post;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.auth.Login;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;
import toy.board.service.post.dto.PostResponse;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    @DisplayName("Post 엔티티의 comments에 대한 CommentListDto 생성시 추가적인 쿼리발생 X")
    @Test
    public void 정상작동테스트_추가적인_쿼리_발생_x() throws Exception {
        //given
        Post post = PostTest.create("username", "nickname");
        Member member = post.getWriter();
        em.persist(member);
        em.persist(post);
        Long postId = post.getId();

        int commentCount = 10;
        int replyCount = 2;
        createComment(post, member, commentCount, replyCount);

        em.flush();
        em.clear();

        //when
        Post findPost = postRepository.findPostWithFetchJoinWriterAndProfileAndComments(postId)
                .get();
        System.out.println();

        //then 추가적인 쿼리 1회만 발생함
        for (Comment comment : findPost.getComments().stream().filter(Comment::isCommentType)
                .toList()) {
            System.out.println("comment = " + comment);
            System.out.println("comment.getReplies().get(0) = " + comment.getReplies().get(0));
        }
    }

    private void createComment(Post post, Member member, int commentCount, int replyCount) {
        for (int i = 0; i < commentCount; i++) {
            Comment comment = new Comment(post, member, "content" + i, CommentType.COMMENT, null);
            em.persist(comment);
            createReply(post, member, replyCount, i, comment);
        }
    }

    private void createReply(Post post, Member member, int replyCount, int i, Comment comment) {
        for (int j = 0; j < replyCount; j++) {
            Comment reply = new Comment(post, member, "content" + i + j, CommentType.REPLY,
                    comment);
            em.persist(reply);
        }
    }

    @DisplayName("jpa fetch join test: memberId와 일치하는 writerId를 갖는 Post 페이징")
    @Test
    public void 실행테스트_findAllByWriterId() throws Exception {
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
        Page<Post> page = postRepository.findAllByWriterIdFetchJoinWriterAndProfile(memberId,
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

    @DisplayName("jpa fetch join test: repository를 통해 post 가져올 때 member와 profile도 함께 가져온다.")
    @Test
    public void whenFindPost_thenFindMemberAndProfile() throws Exception {
        //given
        Post post = PostTest.create("random", "random");
        em.persist(post.getWriter());
        em.persist(post);
        Long postId = post.getId();

        em.flush();
        em.clear();

        //when
        Optional<Post> findPost = postRepository.findPostWithFetchJoinWriterAndProfile(postId);

        //then. 쿼리가 발생하지 않음 확인 완료
        assertThat(findPost.isPresent()).isTrue();
        System.out.println("findPost 엔티티 프로퍼티 조회");
        System.out.println("findPost.get().getWriter() = " + findPost.get().getWriter());
        System.out.println(
                "findPost.get().getWriterNickname() = " + findPost.get().getWriterNickname());
    }

    @DisplayName("탈퇴한 회원의 post를 정상적으로 가져온다.")
    @Test
    public void 정상테스트_탈퇴한_회원_게시물() throws Exception {
        //given
        Post post = PostTest.create("random", "random");
        Comment comment = new Comment(post, post.getWriter(), "content", CommentType.COMMENT, null);

        em.persist(post.getWriter());
        em.persist(post);
        em.persist(comment);
        Long postId = post.getId();

        post.getWriter().changeAllPostAndCommentWriterToNull();
        em.flush();
        em.clear();

        //when
        Optional<Post> findPost = postRepository.findPostWithFetchJoinWriterAndProfileAndComments(
                postId);

        //then. 쿼리가 발생하지 않음 확인 완료
        assertThat(findPost.isPresent()).isTrue();
        Post findPostGet = findPost.get();

        assertThat(findPostGet.getComments()).isNotNull();
        System.out.println(
                "findPost.get().getComments().get(0) = " + findPostGet.getComments().get(0));

        assertThatNoException()
                .isThrownBy(() ->
                        PostResponse.of(findPostGet)
                );
        PostResponse postDto = PostResponse.of(findPostGet);
        System.out.println("postDto = " + postDto);
    }

    @DisplayName("fetch join test: Post만 반환값으로 받고 엔티티 그래프를 이용할 수 있도록 한다.")
    @Test
    public void whenFetchJoinPost_thenUsingEntityThatRelated() throws Exception {
        //given
        insertMemberAndPost(10, 2);

        List<Tuple> posts = queryFactory
                .select(post, comment.count())
                .from(post)
                .leftJoin(comment).on(comment.post.eq(post)).groupBy(post)
                .fetch();

        //when
        for (Tuple postAndCommentCount : posts) {
            Post post = postAndCommentCount.get(0, Post.class);
            Long commentCount = postAndCommentCount.get(1, Long.class);

            System.out.println("post.getWriterId() = " + post.getWriterId());
            System.out.println(
                    "post.getWriter() = " + post.getWriter());
            System.out.println("commentCount = " + commentCount);
        }

        //then
    }

    @DisplayName("spring data jpa를 사용한 findAll이 정상동작")
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

        Page<Post> page = postRepository.findAllWithFetchJoinWriterAndProfile(pageable);

        // for문을 돌며 Member 와 Profile에 접근해도 추가적인 쿼리가 발생하지 않는다.
        for (Post post : page.getContent()) {
            System.out.println("==============================");
            System.out.println("post.getWriterNickname() = " + post.getWriterNickname());
        }

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
        Login login = new Login("password");
        Profile profile = new Profile("nick" + index);

        Member member = Member.builder(
                "email" + index,
                login,
                profile,
                LoginType.LOCAL_LOGIN,
                UserRole.USER
        ).build();

        em.persist(member);
        return member;
    }
}