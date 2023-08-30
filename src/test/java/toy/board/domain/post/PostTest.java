package toy.board.domain.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

    @DisplayName("")
    @Test
    public void notNull() throws Exception {


        //given

        //when

        //then
    }

    public static Post create() {
        return new Post(1L, "writer", "title", "content");
    }

}