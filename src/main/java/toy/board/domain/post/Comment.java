package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import toy.board.domain.BaseDeleteEntity;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"replies"})
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
            @NotNull final CommentType type,
            final Comment parent
    ) {

        this.post = post;
        this.wtiterId = writerId;
        this.writer = writer;
        this.content = content;
        this.type = type;

        if (type == CommentType.REPLY) {
            parent.receiveReply(this);
        }
    }

    public void update(@NotBlank final String content, final Long writerId) {
        validateRight(writerId);
        this.content = content;
    }
    private void receiveReply(final Comment reply) {
        validateType(reply);
        this.replies.add(reply);
        reply.parent = this;
    }

    public void validateRight(final Long writerId) {
        if (!writerId.equals(this.wtiterId)) {
            throw new BusinessException(ExceptionCode.COMMENT_NOT_WRITER);
        }
    }

    private void validateType(final Comment reply) {
        if (this.type != CommentType.COMMENT || reply.type != CommentType.REPLY) {
            throw new BusinessException(ExceptionCode.COMMENT_INVALID_TYPE);
        }
    }
}
