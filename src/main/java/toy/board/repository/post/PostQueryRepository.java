package toy.board.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.controller.post.dto.PostListDto;

public interface PostQueryRepository {

    Page<PostListDto> findAllPost(Pageable pageable);
}
