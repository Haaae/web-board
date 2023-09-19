package toy.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@MappedSuperclass
@Getter
public class BaseDeleteEntity extends BaseEntity {

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    public void delete() {
        this.isDeleted = true;
        deletedDate = LocalDateTime.now();
    }
}
