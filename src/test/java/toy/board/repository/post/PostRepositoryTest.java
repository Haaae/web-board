package toy.board.repository.post;

import static org.assertj.core.api.Assertions.*;
import static toy.board.domain.post.QComment.comment;
import static toy.board.domain.post.QPost.post;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.controller.post.dto.PostDto;
import toy.board.domain.auth.Login;
import toy.board.domain.post.Post;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    @BeforeEach
    void setup() {
        insertMemberAndPost(10, 2);
    }

    private void insertMemberAndPost(int memberNum, int postNum) {
        for (int i = 0; i < memberNum; i++) {
            Login login = new Login("password");

            StringBuilder memberEmailPrefix = new StringBuilder("email");
            StringBuilder memberNicknamePrefix = new StringBuilder("nick");
            StringBuilder titlePrefix = new StringBuilder("title");

            Member member = Member.builder(
                    memberEmailPrefix.append(i).toString(),
                    login,
                    Profile.builder(memberNicknamePrefix.append(i).toString()).build(),
                    LoginType.LOCAL_LOGIN,
                    UserRole.USER
            ).build();

            em.persist(member);

            for (int j = 0; j < postNum; j++) {
                Post post = new Post(
                        member.getId(),
                        member.getProfile().getNickname(),
                        titlePrefix.append(j).toString(),
                        "content"
                );

                em.persist(post);
            }
            em.flush();
            em.clear();
        }
    }
    
    @DisplayName("fetch join test: Post만 반환값으로 받고 엔티티 그래프를 이용할 수 있도록 한다.")
    @Test
    public void whenFetchJoinPost_thenUsingEntityThatRelated() throws  Exception {
        //given
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

    @DisplayName("@ManyToOne과 fetch join을 사용한 findAll(Pageable pageable)이 정상동작")
    @Test
    public void whenFindAll_thenSuccess() throws  Exception {
        //given
        PageRequest pageable = PageRequest.of(1, 10);

        //when
        Page<PostDto> page = postRepository.findAllPost(pageable);

        for (PostDto postListDto : page.getContent()) {
            System.out.println("=================================================================");
            System.out.println("postListDto = " + postListDto);
            System.out.println("postListDto.writer = " + postListDto.writer());
            System.out.println("postListDto.writerId = " + postListDto.writerId());
            System.out.println("postListDto.isModified() = " + postListDto.isModified());
        }

        //then
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(20);

        System.out.println("==============================================");
        System.out.println("==============================================");
        System.out.println("==============================================");
        System.out.println("page = " + page);
    }
}