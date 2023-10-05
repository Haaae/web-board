package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import toy.board.domain.base.BaseDeleteEntity;
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

    @Column(name = "writer_id")
    private Long writerId;

    @Column(name = "writer")
    private String writer;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", updatable = false)
    private Comment parent;


    public Comment(
            @NotNull final Post post,
            @NotNull final Long writerId,
            @NotBlank final String writer,
            @NotNull final String content,
            @NotNull final CommentType type,
            final Comment parent
    ) {
        this.post = post;
        this.writerId = writerId;
        this.writer = writer;
        this.content = content;
        this.type = type;

        validate(parent);

        if (type == CommentType.REPLY) {
            this.leaveReply(parent);
        }
    }

    public void update(@NotBlank final String content, final Long writerId) {
        validateRight(writerId);
        this.content = content;
    }

    public void validateRight(final Long writerId) {
        if (!writerId.equals(this.writerId)) {
            throw new BusinessException(ExceptionCode.COMMENT_NOT_WRITER);
        }
    }

    private void leaveReply(final Comment parent) {
        this.parent = parent;
        parent.replies.add(this);
    }

    private void validate(final Comment parent) {
        if (this.type == CommentType.COMMENT && parent != null) {
            throw new BusinessException(ExceptionCode.COMMENT_CAN_NOT_HAVE_PARENT);
        }

        if (this.type == CommentType.REPLY && parent == null) {
            throw new BusinessException(ExceptionCode.NULL_COMMENT);
        }

        if (this.type == CommentType.REPLY && parent.type == CommentType.REPLY) {
            throw new BusinessException(ExceptionCode.INVALID_COMMENT_TYPE);
        }
    }
}
