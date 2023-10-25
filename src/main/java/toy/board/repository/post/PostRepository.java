package toy.board.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.post.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

    /**
     * Post 반환 시 Member, Profile을 fetch join한다.
     * ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고,
     * 연계하여 fetch join할 수 있다.
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Query(value = """
            SELECT p FROM Post p 
            LEFT JOIN FETCH p.writer AS w 
            LEFT JOIN FETCH w.profile 
            WHERE p.id = :postId
            """
    )
    Optional<Post> findPostById(@Param("postId") final Long id);

    /**
     * Post를 페이징 처리하여 Page<Post>로 반환한다.
     * 이때 Member와 Profile을 fetch join한다.
     *
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be
     *                 {@literal null}.
     */
    @Query(
            value = """
                    SELECT p FROM Post p 
                    LEFT JOIN FETCH p.writer AS w 
                    LEFT JOIN FETCH w.profile
                    """,
            countQuery = "SELECT count(p) FROM Post p"
    )
    @Override
    Page<Post> findAll(Pageable pageable);
}
