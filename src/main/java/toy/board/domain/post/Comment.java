package toy.board.domain.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import toy.board.domain.base.BaseDeleteEntity;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer")
    private Member writer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", updatable = false)
    private Comment parent;


    public Comment(
            @NotNull final Post post,
            @NotBlank final Member writer,
            @NotNull final String content,
            @NotNull final CommentType type,
            final Comment parent
    ) {
        addCommentTo(post);
        addCommentTo(writer);
        this.content = content;
        this.type = type;

        validate(parent);

        if (type == CommentType.REPLY) {
            this.leaveReply(parent);
        }
    }

    /**
     * Member와 Comment의 양방향 매핑을 위한 메서드.
     * @param writer Comment 작성자.
     */
    private void addCommentTo(Member writer) {
        this.writer = writer;
        writer.addComment(this);
    }

    /**
     * Post와 Comment의 양방향 매핑을 위한 메서드
     * @param post Comment가 소속된 게시물.
     */
    private void addCommentTo(Post post) {
        this.post = post;
        post.addComment(this);
    }

    public void update(@NotBlank final String content, @NotNull final Long writerId) {
        validateRight(writerId);
        this.content = content;
    }

    public void validateRight(final Long writerId) {
        if (!writerId.equals(this.writer.getId())) {
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
