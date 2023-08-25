package toy.board.domain.auth;

import jakarta.persistence.*;
import lombok.Getter;
import toy.board.domain.BaseTimeEntity;

@Entity
@Getter
public class Cidi extends BaseTimeEntity {

    public static final int CI_LENGTH = 88;
    public static final int DI_LENGTH = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cidi_id")
    private Long id;

    @Column(name = "ci", length = CI_LENGTH, nullable = false, unique = true)
    private String ci;

    @Column(name = "di", length = DI_LENGTH, nullable = false, unique = true)
    private String di;
}
