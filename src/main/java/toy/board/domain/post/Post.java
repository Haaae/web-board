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
import toy.board.domain.base.BaseEntity;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.utils.Assert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Post extends BaseEntity {

    public static final int TITLE_MAX_LENGTH = 50;
    public static final int CONTENT_MAX_LENGTH = 10000;

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

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    @ToString.Exclude
    private final List<Comment> comments = new ArrayList<>();

    public Post(
            @NotNull final Member writer,
            @NotBlank final String title,
            @NotBlank final String content
    ) {

        validate(writer, title, content);

        // Member writer에 대한 양방향 매핑
        addTo(writer);

        this.title = title;
        this.content = content;
        this.hits = 0L;
        this.isEdited = false;
    }

    private void validate(final Member writer, final String title, final String content) {
        Assert.notNull(writer);
        Assert.hasTextAndLength(title, TITLE_MAX_LENGTH);
        Assert.hasTextAndLength(content, CONTENT_MAX_LENGTH);
    }

    /**
     * Comment와 Post의 양방향 매핑을 위한 메서드. Comment에서 호출한다.
     *
     * @param comment Post에 포함될 새로운 메서드
     */
    public void addComment(final Comment comment) {
        this.comments.add(comment);
    }

    /**
     * Post와 Member의 양방향 매핑을 위한 메서드.
     *
     * @param writer Post 작성자.
     */
    private void addTo(final Member writer) {
        this.writer = writer;
        writer.addPost(this);
    }

    public void update(@NotBlank final String content, @NotNull final Member writer) {
        Assert.hasTextAndLength(content, CONTENT_MAX_LENGTH);
        Assert.notNull(writer);

        validateRight(writer);

        this.content = content;
        this.isEdited = true;
    }

    public void validateRight(final Member writer) {
        if (writer.hasDeleteRight()) {
            return;
        }

        validateIsWriter(writer);
    }

    public void applyWriterWithdrawal(final Member writer) {
        validateIsWriter(writer);

        this.writer = null;
    }

    private void validateIsWriter(Member writer) {
        if (this.writer == null || !this.writer.equals(writer)) {
            throw new BusinessException(ExceptionCode.INVALID_AUTHORITY);
        }
    }

    public long increaseHits() {
        return this.hits++;
    }

    public int countComments() {
        return (int) this.comments
                .stream()
                .filter(comment -> !comment.isDeleted())
                .count();
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(
                this.comments
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
}
