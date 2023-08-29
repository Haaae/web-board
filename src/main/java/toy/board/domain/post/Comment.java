package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    @Column(name = "writer_id") // 작성자가 탈퇴할 경우 null로 변경해야 한다.
    private Long wtiterId;

    @Column(name = "writer")    // 작성자가 탈퇴할 경우 null로 변경해야 한다.
    private String writer;

    @OneToMany(mappedBy = "parent")
    private List<Comment> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", updatable = false)
    private Comment parent;

    /**
     * 양방향 관계인 Member와 Post에 대해 자동으로 양방향 매핑을 수행한다.
     */
    public Comment(
            @NotNull final Post post,
            @NotNull final Long writerId,
            @NotBlank final String writer,
            @NotNull final String content,
            @NotNull final CommentType type
    ) {

        this.post = post;
        this.wtiterId = writerId;
        this.writer = writer;
        this.content = content;
        this.type = type;
    }

    public Comment(
            @NotNull final Post post,
            @NotNull final Long writerId,
            @NotBlank final String writer,
            @NotNull final String content,
            @NotNull final Comment comment
    ) {
        this(post, writerId, writer, content, CommentType.REPLY);
        comment.leaveReply(this);
    }

    public boolean update(final String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }

        this.content = content;
        return true;
    }

    public void leaveReply(Comment reply) {
        if (areTypesCorrectThisAnd(reply)) {
            throw new IllegalArgumentException("주어진 댓글과 대댓글의 타입이 올바르지 않습니다.");
        }

        if (hasComment(reply)) {
            throw new IllegalArgumentException("대댓글이 이미 다른 댓글에 포섭되어 있습니다.");
        }

        if (isNew(reply)) {
            throw new IllegalArgumentException("댓글이 이미 해당 대댓글을 포함하고 있습니다.");
        }

        this.replies.add(reply);
        reply.parent = this;
    }

    private boolean isNew(Comment reply) {
        return this.replies.contains(reply);
    }

    private static boolean hasComment(Comment reply) {
        return reply.parent != null;
    }

    private boolean areTypesCorrectThisAnd(Comment reply) {
        return this.type != CommentType.COMMENT || reply.type != CommentType.REPLY;
    }
}
