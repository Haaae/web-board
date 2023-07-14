package toy.board.domain.login.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;

import toy.board.BaseEntity;

@Entity
@Table(catalog = "auth")
@Getter
public class Login  extends BaseEntity {

    @Transient
    private static final int USER_ID_LENGTH = 20;
    @Transient
    private static final int SOLT_LENGTH = 128;
    @Transient
    private static final int PASSWORD_LENGTH = 128;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long id;

    @Column(name = "id", nullable = false, length = USER_ID_LENGTH)
    private String userId;

    @Column(name = "solt", nullable = false, length = PASSWORD_LENGTH)
    private String solt;
}
