package toy.board.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.board.domain.post.Post;
import toy.board.service.post.dto.PostDto;

import java.util.Optional;

public interface PostQueryRepository {

    Page<PostDto> findAllPost(Pageable pageable);

    Optional<PostDto> getPostDtoById(Long postId);

    Optional<Post> findPostByIdWithFetchComments(Long postId);
}
