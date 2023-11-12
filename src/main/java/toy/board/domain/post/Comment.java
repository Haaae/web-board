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
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
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
@ToString
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

    @Column(name = "isEdited", nullable = false)
    private boolean isEdited;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", updatable = false)
    @ToString.Exclude
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
        this.isEdited = false;

        validateType(parent);

        if (type == CommentType.REPLY) {
            validatePostOfParentComment(parent);
            this.leaveReply(parent);
        }
    }

    /**
     * Member와 Comment의 양방향 매핑을 위한 메서드.
     *
     * @param writer Comment 작성자.
     */
    private void addCommentTo(final Member writer) {
        this.writer = writer;
        writer.addComment(this);
    }

    /**
     * Post와 Comment의 양방향 매핑을 위한 메서드
     *
     * @param post Comment가 소속된 게시물.
     */
    private void addCommentTo(final Post post) {
        this.post = post;
        post.addComment(this);
    }


    private void leaveReply(final Comment parent) {
        this.parent = parent;
        parent.replies.add(this);
    }

    private void validateType(final Comment parent) {
        if (isNotValidType(parent)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);
        }
    }

    private void validatePostOfParentComment(final Comment parent) {
        if (!parent.post.equals(this.post)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_POST_OF_COMMENT);
        }
    }

    private boolean isNotValidType(final Comment parent) {
        return !(isValidComment(parent) || isValidReply(parent));
    }

    private boolean isValidReply(final Comment parent) {
        return this.type == CommentType.REPLY && parent != null
                && parent.type == CommentType.COMMENT;
    }

    private boolean isValidComment(final Comment parent) {
        return this.type == CommentType.COMMENT && parent == null;
    }

    public void update(@NotBlank final String content, @NotNull final Member writer) {
        validateRight(writer);
        this.content = content;
        this.isEdited = true;
    }

    public void validateRight(final Member writer) {
        if (writer.hasDeleteRight()) {
            return;
        }

        if (!writer.equals(this.writer)) {
            throw new BusinessException(ExceptionCode.INVALID_AUTHORITY);
        }
    }

    public void applyWriterWithdrawal() {
        this.writer = null;
    }

    public boolean isCommentType() {
        return this.type == CommentType.COMMENT;
    }

    public List<Comment> getReplies() {
        return Collections.unmodifiableList(
                this.replies
        );
    }

    public Long getWriterId() {
        if (this.writer == null) {
            return null;
        }
        return this.writer.getId();
    }

    public String getWriterNickname() {
        if (this.writer == null) {
            return null;
        }
        return this.writer.getNickname();
    }

    public Long getPostId() {
        return this.post.getId();
    }
}
