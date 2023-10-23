package toy.board.domain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import toy.board.domain.base.BaseDeleteEntity;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "writer")
//    private Profile writer;

    public Post(
//            @NotNull final Member writerId,
            @NotNull final Member writer,
            @NotBlank final String title,
            @NotBlank final String content
    ) {

        this.writer = writer;
        this.title = title;
        this.content = content;
        this.hits = 0L;
    }

    public void update(@NotBlank final String content, @NotNull final Long writer) {
        validateRight(writer);
        this.content = content;
    }

    public void validateRight(final Long writer) {
        if (!writer.equals(this.writer.getId())) {
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
        return this.writer.getProfile().getNickname();
    }
}
