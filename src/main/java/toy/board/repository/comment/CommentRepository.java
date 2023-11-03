package toy.board.repository.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    /**
     * Comment 반환 시 Member, Profile을 fetch join한다.
     * ~ToOne 매핑관계에 대한 fetch join은 별명을 사용할 수 있고,
     * 연계하여 fetch join할 수 있다.
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Query(value = "SELECT c FROM Comment c "
            + "LEFT JOIN FETCH c.writer AS w "
            + "LEFT JOIN FETCH w.profile "
            + "WHERE c.id = :commentId"
    )
    Optional<Comment> findCommentById(@Param("commentId") final Long id);

    /**
     * WriterId가 memberId와 같은 Comment를 페이징 처리하여 Page<Post>로 반환한다. 이때 Member와 Profile, Post를 fetch join한다.
     *
     * @param writerId writerId가 일치하는 Post들을 반환한다.
     * @param pageable 페이징 정보
     */
    @Query(value = """
            SELECT c FROM Comment c 
            LEFT JOIN FETCH c.post 
            LEFT JOIN FETCH c.writer AS w 
            LEFT JOIN FETCH w.profile 
            WHERE w.id = :writerId AND NOT c.isDeleted
            """,
            countQuery = "SELECT count(c) FROM Comment c WHERE c.writer.id = :writerId"
    )
    Page<Comment> findAllByWriterId(@Param("writerId") Long writerId, Pageable pageable);

    void deleteCommentsByPost(final Post post);

    void deleteCommentsByPostAndType(final Post post, final CommentType type);

}
