package toy.board.entity.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.util.StringUtils;
import toy.board.entity.BaseDeleteEntity;
import toy.board.entity.user.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member"})
public class Post extends BaseDeleteEntity {

    public static final int TITLE_MAX_LENGTH = 10000;
    public static final int CONTENT_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = TITLE_MAX_LENGTH, updatable = false)
    private String title;

    @Column(name = "content", nullable = false, length = CONTENT_LENGTH)
    private String content;

    @Column(name = "hits", nullable = false)
    private Long hits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    /**
     * 양방향 관계인 Member에 대해 자동으로 양방향 매핑을 수행한다.
     */
    public Post(@NotNull final Member member, final String title, final String content) {

        if (member == null) {
            throw new IllegalArgumentException("Member must not be null. field: " + this.getClass());
        }

        this.member = member;
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
