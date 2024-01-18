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
import toy.board.utils.Assert;

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

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
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
        // 파라미터 유효성 및 Comment 타입에 따른 유효성 검증
        validate(post, writer, content, type, parent);

        // 필드 변수 초기화
        this.content = content;
        this.type = type;
        this.isEdited = false;

        // Post, Member, parent comment에 대한 양방향 매핑
        this.addTo(post);
        this.addTo(writer);
        if (this.isReplyType()) {
            this.leaveReply(parent);
        }
    }

    private void validate(
            final Post post,
            final Member writer,
            final String content,
            final CommentType type,
            final Comment parent
    ) {
        Assert.notNull(post);
        Assert.notNull(writer);
        Assert.notNull(type);
        Assert.hasTextAndLength(content, CONTENT_LENGTH);

        validateType(parent, type, post);
    }

    /**
     * Member와 Comment의 양방향 매핑을 위한 메서드.
     *
     * @param writer Comment 작성자.
     */
    private void addTo(final Member writer) {
        this.writer = writer;
        writer.addComment(this);
    }

    /**
     * Post와 Comment의 양방향 매핑을 위한 메서드
     *
     * @param post Comment가 소속된 게시물.
     */
    private void addTo(final Post post) {
        this.post = post;
        post.addComment(this);
    }


    private void leaveReply(final Comment parent) {
        this.parent = parent;
        parent.replies.add(this);
    }

    private void validateType(final Comment parent, final CommentType type, final Post post) {
        if (isNotValidComment(parent, type) && isNotValidReply(parent, type)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);
        }

        // this가 reply 타입이고, parent comment의 post가 this와 다를 경우 예외 발생
        if (type == CommentType.REPLY && !parent.post.equals(post)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_POST_OF_COMMENT);
        }
    }

    private boolean isNotValidReply(final Comment parent, final CommentType type) {
        return !(type == CommentType.REPLY && parent != null && parent.isCommentType());
    }

    private boolean isNotValidComment(final Comment parent, final CommentType type) {
        return !(type == CommentType.COMMENT && parent == null);
    }

    public void update(@NotBlank final String content, final Member writer) {
        Assert.hasText(content);

        validateIsWriter(writer);

        // 수정 내용이 기존 내용과 다르고, 작성자가 맞을 때
        if (!this.content.equals(content)) {
            this.content = content;
            this.isEdited = true;
        }
    }

    /**
     * Post가 삭제되지 않은 상태에서 Comment가 삭제될 때 Comment가 삭제되었음을 알려주기 위한 상태이다. 만약 Post도 삭제되면 Post와 그에 소속된 Comment 모두 DB에서 삭제된다.
     *
     * @param member
     */
    public void deleteBy(@NotNull final Member member) {
        Assert.notNull(member);

        if (this.isDeleted()) {
            return;
        }

        validateDeleteRight(member);
        this.delete();
    }

    /**
     * 작성자가 회원탈퇴하면 이후 같은 닉네임의 사용자가 회원가입할 수 있기 때문에 댓글의 작성자를 null로 만들어야 한다. 이때 사용하는 메서드이다.
     *
     * @param writer
     */
    public void applyWriterWithdrawal(final Member writer) {
        validateIsWriter(writer);
        this.writer = null;
    }

    private void validateDeleteRight(final Member writer) {
        if (writer.hasDeleteRight()) {
            return;
        }

        validateIsWriter(writer);
    }
    
    private void validateIsWriter(Member writer) {
        if (this.writer == null || !this.writer.equals(writer)) {
            throw new BusinessException(ExceptionCode.INVALID_AUTHORITY);
        }
    }

    public boolean isCommentType() {
        return this.type == CommentType.COMMENT;
    }

    public boolean isReplyType() {
        return this.type == CommentType.REPLY;
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
