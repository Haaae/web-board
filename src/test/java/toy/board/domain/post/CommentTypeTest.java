package toy.board.domain.post;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTypeTest {
    
    @DisplayName("타입에 따른 생성자 구분")
    @Test
    public void CommentTypeTest() throws  Exception {
        //given
        Post post = PostTest.create();
        String content = "content";
        long memberId = 1L;
        //when
        CommentType commentType = CommentType.COMMENT;
        CommentType replyType = CommentType.REPLY;

        //then
        Comment comment = commentType.create(post, memberId, post.getWriter(), content, null);
        Comment comment2 = replyType.create(post, memberId, post.getWriter(), content, comment);

        System.out.println("comment = " + comment);
        System.out.println("comment2 = " + comment2);
    }

}