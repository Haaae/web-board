package toy.board.domain.login.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import toy.board.BaseEntity;
import toy.board.domain.login.entity.auth.Cidi;
import toy.board.domain.login.entity.auth.Login;
import toy.board.domain.login.entity.auth.SocialLogin;

@Entity
@Getter
@Table(catalog = "user")
public class Member extends BaseEntity {
    @Transient
    private static final int USER_ID_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authentication_id", unique = true)
    private Authentication authentication;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_login_id", unique = true)
    private SocialLogin socialLogin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_id", unique = true)
    private Login login;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cidi_id", unique = true)
    private Cidi cidi;
}

