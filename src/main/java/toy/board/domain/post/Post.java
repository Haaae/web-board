package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.util.StringUtils;
import toy.board.domain.BaseDeleteEntity;
import toy.board.domain.user.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member"})
public class Post extends BaseDeleteEntity {

    public static final int TITLE_MAX_LENGTH = 10000;
    public static final int CONTENT_MAX_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = TITLE_MAX_LENGTH, updatable = false)
    private String title;

    @Column(name = "content", nullable = false, length = CONTENT_MAX_LENGTH)
    private String content;

    @Column(name = "hits", nullable = false)
    private Long hits;

    @Column(name = "writer_id") // 작성자가 탈퇴할 경우 null로 변경해야 한다.
    private Long writerId;

    @Column(name = "writer")    // 작성자가 탈퇴할 경우 null로 변경해야 한다.
    private String writer;

    /**
     * 양방향 관계인 Member에 대해 자동으로 양방향 매핑을 수행한다.
     */
    public Post(
            @NotNull final Long writerId,
            @NotNull final String writer,
            @NotBlank final String title,
            @NotBlank final String content
    ) {

        this.writerId = writerId;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.hits = 0L;
    }

    public boolean update(final String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }

        this.content = content;
        return true;
    }

    public long increaseHits() {
        return this.hits++;
    }
}
