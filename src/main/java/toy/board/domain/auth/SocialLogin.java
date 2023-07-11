package toy.board.domain.auth;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(catalog = "auth")
@Getter
public class SocialLogin {

    @Transient
    private static final int EXTERNAL_ID_LENGTH = 64;
    @Transient
    private static final int ACCESS_TOKEN_LENGTH = 256;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_login_id")
    private Long id;

    @Column(name = "social_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialCode socialCode;

    @Column(name = "external_id", nullable = false, length = EXTERNAL_ID_LENGTH)
    private String externalId;

    @Column(name = "access_token", nullable = false, length = ACCESS_TOKEN_LENGTH)
    private String accessToken;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
}
