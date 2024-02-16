package toy.board.service.post.dto;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.repository.post.PostRepository;

@SpringBootTest
@Transactional
class CommentListDtoTest {

    @Autowired
    EntityManager em;
    @Autowired
    PostRepository postRepository;

    @DisplayName("CommentListDto 생성 시 총 dto 개수 카운트 검증")
    @Test
    public void 정상작동테스트_전체_dto_카운트() throws Exception {
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
        int totalCommentCount = commentCount * (replyCount + 1);
        Post findPost = postRepository.findPostWithFetchJoinWriter(postId).get();
        CommentsResponse commentListDto = CommentsResponse.of(findPost);

        //then
        Assertions.assertThat(commentListDto.count()).isEqualTo(totalCommentCount);
        Assertions.assertThat(post.getComments().size()).isEqualTo(totalCommentCount);
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
}