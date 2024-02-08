package toy.board.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@MappedSuperclass
@lombok.Getter
public class BaseDeleteEntity extends BaseEntity {

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    protected void delete() {
        this.isDeleted = true;
        deletedDate = LocalDateTime.now();
    }
}
