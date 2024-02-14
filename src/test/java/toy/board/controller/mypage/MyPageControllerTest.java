package toy.board.controller.mypage;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import toy.board.constant.SessionConst;
import toy.board.controller.mypage.dto.response.MyInfoResponse;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentTest;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.exception.ErrorResponse;
import toy.board.exception.ExceptionCode;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    private final MockHttpSession session = new MockHttpSession();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    @Nested
    class GetMyPageTest {

        @DisplayName("회원 정보 불러오기 성공")
        @Test
        void 회원_정보_불러오기_성공() throws Exception {
            //given
            String username = "username";
            String nickname = "nickname";
            int countOfPost = 5;
            int countOfExistComment = 3;

            Member member = MemberTest.create(username, nickname, UserRole.USER);
            em.persist(member);

            // post 생성
            Post post = null;
            for (int i = 0; i < countOfPost; i++) {
                Post tmp = PostTest.create(member);
                em.persist(tmp);
                post = tmp;
            }

            // 댓글 생성 후 하나 삭제
            Comment comment = null;
            for (int i = 0; i < countOfExistComment + 1; i++) {
                Comment tmp = CommentTest.create(post, member, CommentType.COMMENT);
                em.persist(tmp);
                comment = tmp;
            }
            comment.deleteBy(member);   // 댓글 하나 삭제

            //when
            session.setAttribute(SessionConst.LOGIN_MEMBER, member.getId());
            String url = "/mypage";

            //then
            MvcResult mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get(url)
                                    .session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isOk()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    )
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            MyInfoResponse myInfoResponse = mapper.readValue(contentAsString, MyInfoResponse.class);
            assertThat(myInfoResponse.username()).isEqualTo(username);
            assertThat(myInfoResponse.nickname()).isEqualTo(nickname);
            assertThat(myInfoResponse.postCount()).isEqualTo(countOfPost);
            assertThat(myInfoResponse.commentCount()).isEqualTo(countOfExistComment);
        }

        @DisplayName("회원 정보 불러오기 실패 : session에 정보가 없으면 에러코드 반환")
        @Test
        void 회원_정보_호출시_session에_로그인_정보가_없으면_에러코드_반환() throws Exception {
            //given
            ExceptionCode exceptionCode = ExceptionCode.BAD_REQUEST_AUTHENTICATION;

            //when
            String url = "/mypage";

            //then
            MvcResult mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get(url)
                                    .session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isUnauthorized()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    )
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);
            assertThat(errorResponse.code()).isEqualTo(exceptionCode.getCode());
        }
    }

    @Nested
    class MyPostTest {

        @DisplayName("작성글 페이징 성공")
        @Test
        void 작성글_페이징_성공() throws Exception {
            //given
            int countOfPost = 5;
            int size = 2;
            int pageNumber = 1;

            Member member = MemberTest.create();
            em.persist(member);

            // post 생성
            for (int i = 0; i < countOfPost; i++) {
                em.persist(
                        PostTest.create(member)
                );
            }

            //when
            session.setAttribute(SessionConst.LOGIN_MEMBER, member.getId());
            String url = String.format("/mypage/posts?size=%d&page=%d", size, pageNumber);

            //then
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get(url)
                                    .session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isOk()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    );
        }

        @DisplayName("게시글 페이징 실패 : session에 정보가 없으면 에러코드 반환")
        @Test
        void 게시글_페이징시_session에_로그인_정보가_없으면_에러코드_반환() throws Exception {
            //given
            ExceptionCode exceptionCode = ExceptionCode.BAD_REQUEST_AUTHENTICATION;

            //when
            int size = 2;
            int pageNumber = 1;
            String url = String.format("/mypage/posts?size=%d&page=%d", size, pageNumber);

            //then
            MvcResult mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get(url)
                                    .session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isUnauthorized()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    )
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);
            assertThat(errorResponse.code()).isEqualTo(exceptionCode.getCode());
        }
    }

    @Nested
    class MyCommentTest {

        @DisplayName("댓글 페이징 성공")
        @Test
        void 댓글_페이징_성공() throws Exception {
            //given
            int countOfComment = 5;
            int size = 2;
            int pageNumber = 1;

            Member member = MemberTest.create();
            Post post = PostTest.create(member);
            em.persist(member);
            em.persist(post);

            // comment 생성
            for (int i = 0; i < countOfComment; i++) {
                em.persist(
                        CommentTest.create(post, member, CommentType.COMMENT)
                );
            }

            //when
            session.setAttribute(SessionConst.LOGIN_MEMBER, member.getId());
            String url = String.format("/mypage/comments?size=%d&page=%d", size, pageNumber);

            //then
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get(url)
                                    .session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isOk()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    );
        }

        @DisplayName("댓글 페이징 실패 : session에 정보가 없으면 에러코드 반환")
        @Test
        void 댓글_페이징시_session에_로그인_정보가_없으면_에러코드_반환() throws Exception {
            //given
            ExceptionCode exceptionCode = ExceptionCode.BAD_REQUEST_AUTHENTICATION;

            //when
            int size = 2;
            int pageNumber = 1;
            String url = String.format("/mypage/comments?size=%d&page=%d", size, pageNumber);

            //then
            MvcResult mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get(url)
                                    .session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(
                            MockMvcResultMatchers
                                    .status()
                                    .isUnauthorized()
                    )
                    .andDo(
                            MockMvcResultHandlers.print()
                    )
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);
            assertThat(errorResponse.code()).isEqualTo(exceptionCode.getCode());
        }

    }
}