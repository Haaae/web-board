package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import toy.board.domain.base.BaseDeleteEntity;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseDeleteEntity {

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

    @Column(name = "writer_id")
    private Long writerId;

    @Column(name = "writer")
    private String writer;

    public Post(
            @NotNull final Long writerId,
            @NotNull final String writer,
            @NotBlank final String title,
            @NotBlank final String content
    ) {

        this.writerId = writerId;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.hits = 0L;
    }

    public void update(@NotBlank final String content, @NotNull final Long writerId) {
        validateRight(writerId);
        this.content = content;
    }

    public void validateRight(final Long writerId) {
        if (!writerId.equals(this.writerId)) {
            throw new BusinessException(ExceptionCode.POST_NOT_WRITER);
        }
    }

    public long increaseHits() {
        return this.hits++;
    }
}
