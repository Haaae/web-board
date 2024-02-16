package toy.board.controller.post;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.user.Member;
import toy.board.domain.user.UserRole;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PostPageControllerTest {

    public static final String POST_URL = "/posts";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    @DisplayName("잘못된 페이징 정보로 목록을 조회시 400에러를 응답한다.")
    @ParameterizedTest
    @CsvSource({"1,0", "-1,1", "1,two", "two,1"})
    public void response400WhenRequestByInvalidPagingInfo(String page, String size)
            throws Exception {
        //given
        setupWithSavingPostAndComment(10, 3, 2);
        //when
        String url = POST_URL + "?page=" + page + "&size=" + size;
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers.status()
                                .isBadRequest()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    @DisplayName("게시물 목록 조회 성공: 세션이 없어도 성공")
    @Test
    public void getPostList_success() throws Exception {
        //given
        setupWithSavingPostAndComment(10, 3, 2);
        int page = 0;
        int size = 10;
        //when
        String url = POST_URL + "?page=" + page + "&size=" + size;
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers.status()
                                .isOk()
                )
                .andDo(
                        MockMvcResultHandlers.print()
                );
    }

    private void setupWithSavingPostAndComment(
            int countOfPost,
            int countOfCommentPerPost,
            int countOfReplyPerComment
    ) {
        Member member = persistNewMember();

        for (int i = 0; i < countOfPost; i++) {
            Post post = persistNewPost(member, i);

            for (int k = 0; k < countOfCommentPerPost; k++) {
                Comment comment = persistNewComment(post, member, k);

                for (int j = 0; j < countOfReplyPerComment; j++) {
                    persistNewReply(post, member, j, comment);
                }
            }
        }
        em.flush();
        em.clear();
    }

    private Member persistNewMember() {
        Member member = new Member(
                "member",
                "nickname",
                "password",
                UserRole.USER
        );
        em.persist(member);
        return member;
    }

    private Post persistNewPost(Member member, int i) {
        Post post = new Post(member,
                "title" + i,
                "content" + i
        );

        em.persist(post);
        return post;
    }

    private void persistNewReply(Post post, Member member, int j, Comment comment) {
        Comment reply = new Comment(
                post,
                member,
                "reply" + j,
                CommentType.REPLY,
                comment
        );
        em.persist(reply);
    }

    private Comment persistNewComment(Post post, Member member, int k) {
        Comment comment = new Comment(
                post,
                member,
                "comment" + k,
                CommentType.COMMENT,
                null
        );
        em.persist(comment);
        return comment;
    }
}
