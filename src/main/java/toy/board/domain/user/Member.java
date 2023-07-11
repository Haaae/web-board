package toy.board.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import toy.board.domain.auth.Cidi;
import toy.board.domain.auth.Login;
import toy.board.domain.auth.SocialLogin;

@Entity
@Getter
@Table(catalog = "user")
public class Member {
    @Transient
    private static final int USER_ID_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @OneToOne
    @JoinColumn(name = "authentication_id", unique = true)
    private Authentication authentication;

    @OneToOne
    @JoinColumn(name = "social_login_id", unique = true)
    private SocialLogin socialLogin;

    @OneToOne
    @JoinColumn(name = "login_id", unique = true)
    private Login login;

    @OneToOne
    @JoinColumn(name = "cidi_id", unique = true)
    private Cidi cidi;
}

