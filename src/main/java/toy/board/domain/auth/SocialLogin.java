package toy.board.domain.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import toy.board.domain.base.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SocialLogin extends BaseEntity {

    public static final int EXTERNAL_ID_LENGTH = 64;
    public static final int ACCESS_TOKEN_LENGTH = 256;

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
}
