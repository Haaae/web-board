package toy.board.controller.post;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PostDetailControllerTest {

    private static final String PREFIX = "/posts";
    public static final String POST_URL = "/posts";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    @DisplayName("게시물 상세 데이터 조회: 세션 없이 성공")
    @Test
    public void getPostDetail_success() throws Exception {
        //given
        Post post = PostTest.create("username", "nickname");

        em.persist(post.getWriter());
        em.persist(post);

        long postId = post.getId();
        //when
        String url = POST_URL + "/" + postId;
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
}
