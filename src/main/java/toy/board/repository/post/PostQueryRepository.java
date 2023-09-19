package toy.board.repository.post;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.repository.post.dto.PostDto;

public interface PostQueryRepository {

    Page<PostDto> findAllPost(Pageable pageable);

    Optional<PostDto> getPostDtoById(Long postId);
}
