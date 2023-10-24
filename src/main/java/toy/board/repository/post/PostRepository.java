package toy.board.repository.post;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.post.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

    /**
     * Post 반환 시 Member, Profile을 fetch join한다.
     * ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고,
     * 연계하여 fetch join할 수 있다.
     * @param id must not be {@literal null}.
     * @return
     */
    @Query(value = "SELECT p FROM Post p "
            + "LEFT JOIN FETCH p.writer AS w "
            + "LEFT JOIN FETCH w.profile "
            + "WHERE p.id = :postId"

    )
    Optional<Post> findPostById(@Param("postId") final Long id);
}
