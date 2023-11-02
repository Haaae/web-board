package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import toy.board.domain.base.BaseEntity;
import toy.board.domain.user.Member;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    public Post(
            @NotNull final Member writer,
            @NotBlank final String title,
            @NotBlank final String content
    ) {

        addPostTo(writer);
        this.title = title;
        this.content = content;
        this.hits = 0L;
    }

    /**
     * Comment와 Post의 양방향 매핑을 위한 메서드. Comment에서 호출한다.
     *
     * @param comment Post에 포함될 새로운 메서드
     */
    public void addComment(final Comment comment) {
        this.comments.add(comment);
    }

    public void update(@NotBlank final String content, @NotNull final Member writer) {
        validateRight(writer);
        this.content = content;
    }

    public void validateRight(final Member writer) {
        if (writer.hasDeleteRight()) {
            return;
        }

        if (!writer.equals(this.writer)) {
            throw new BusinessException(ExceptionCode.POST_NOT_WRITER);
        }
    }

    public long increaseHits() {
        return this.hits++;
    }

    public Long getWriterId() {
        return this.writer.getId();
    }

    public String getWriterNickname() {
        return this.writer.getNickname();
    }

    /**
     * Post와 Member의 양방향 매핑을 위한 메서드.
     *
     * @param writer Post 작성자.
     */
    private void addPostTo(final Member writer) {
        this.writer = writer;
        writer.addPost(this);
    }

    public void applyWriterWithdrawal() {
        this.writer = null;
    }

    public int countComment() {
        return this.comments.size();
    }
}
