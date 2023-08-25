package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import toy.board.domain.BaseDeleteEntity;
import toy.board.domain.user.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseDeleteEntity {

    public static final int CONTENT_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false, length = CONTENT_LENGTH)
    private String content;

    @Column(name = "type", nullable = false, updatable = false)
    private CommentType type;

    @Column(name = "bundle_id", unique = true, updatable = false)
    private String bundleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    /**
     * 양방향 관계인 Member와 Post에 대해 자동으로 양방향 매핑을 수행한다.
     */
    public Comment(
            @NotNull final Post post,
            @NotNull final Member member,
            @NotNull final String content,
            @NotNull final CommentType type,
            @NotNull final String bundleId
    ) {

        this.post = post;
        this.member = member;
        this.content = content;
        this.type = type;
        this.bundleId = bundleId;
    }

    public boolean update(final String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }

        this.content = content;
        return true;
    }
}
