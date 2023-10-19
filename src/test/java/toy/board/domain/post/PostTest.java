package toy.board.domain.post;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

class PostTest {

    @DisplayName("권한 없는 사용자가 게시물을 수정하려 할 경우 예외 발생")
    @Test
    public void whenUpdatePostWithNotValidWriterId_thenThrowException() throws Exception {
        //given
        Post post = create();
        String newContent = "new";
        //when
        Long invalidWriterId = post.getWriterId() + 1;
        //then
        assertThrows(BusinessException.class,
                () -> post.update(newContent, invalidWriterId),
                ExceptionCode.POST_NOT_WRITER.getDescription());
    }

    @DisplayName("사용자에 의한 게시물 수정 성공")
    @Test
    public void whenUpdatePostWithValidWriterId_thenSuccess() throws Exception {
        //given
        Post post = create();
        String newContent = "new";
        Long writerId = post.getWriterId();
        //when
        post.update(newContent, writerId);
        //then
        assertThat(post.getContent()).isEqualTo(newContent);
    }
    public static Post create() {
        return new Post(1L, "writer", "title", "content");
    }

}