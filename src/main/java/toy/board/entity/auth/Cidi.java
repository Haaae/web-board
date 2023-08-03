package toy.board.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import toy.board.entity.BaseTimeEntity;

@Entity
@Table(catalog = "auth")
@Getter
public class Cidi extends BaseTimeEntity {

    @Transient
    private static final int CI_LENGTH = 88;
    @Transient
    private static final int DI_LENGTH = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cidi_id")
    private Long id;

    @Column(name = "ci", length = CI_LENGTH, nullable = false, unique = true)
    private String ci;

    @Column(name = "di", length = DI_LENGTH, nullable = false, unique = true)
    private String di;
}
