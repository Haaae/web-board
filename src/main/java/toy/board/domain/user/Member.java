package toy.board.domain.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;
import toy.board.domain.auth.Login;
import toy.board.domain.auth.SocialLogin;
import toy.board.domain.base.BaseEntity;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.validator.Validator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder(builderMethodName = "innerBuilder")
public class Member extends BaseEntity {

    public static final int USER_ID_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long id;

    /**
     * 로컬 가입의 경우 유저의 인증 이메일.
     */
    @Column(name = "username", length = USER_ID_LENGTH, nullable = false, unique = true)
    private String username;

    @Column(name = "login_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "user_role", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    @JoinColumn(name = "profile_id", nullable = false, unique = true, updatable = false)
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_login_id", unique = true)
    private SocialLogin socialLogin;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    @JoinColumn(name = "login_id", unique = true)
    @ToString.Exclude
    private Login login;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public static MemberBuilder builder(
            @NotNull final String username,
            @NotNull final Login login,
            @NotNull final Profile profile,
            @NotNull final LoginType loginType,
            @NotNull final UserRole userRole) {

        validate(username, login, profile, loginType, userRole);

        return innerBuilder()
                .username(username)
                .login(login)
                .profile(profile)
                .loginType(loginType)
                .role(userRole);
    }

    private static void validate(
            final String username,
            final Login login,
            final Profile profile,
            final LoginType loginType,
            final UserRole userRole
    ) {
        Validator.hasTextAndLength(username, USER_ID_LENGTH);
        Validator.notNull(login);
        Validator.notNull(profile);
        Validator.notNull(loginType);
        Validator.notNull(userRole);
    }

    public void changeLogin(@NotNull final Login login) {
        Assert.notNull(login, "login mush not be null!");

        this.login = login;
    }

    public void updateRole(Member target) {
        validateRoleEach(target);
        target.role = UserRole.ADMIN;
    }

    public void addPost(final Post post) {
        this.posts.add(post);
    }

    public void addComment(final Comment comment) {
        this.comments.add(comment);
    }

    public boolean hasDeleteRight() {
        return role.isDeleteRight();
    }

    public String getPassword() {
        return this.login.getPassword();
    }

    public void changeAllPostAndCommentWriterToNull() {
        this.posts.forEach(Post::applyWriterWithdrawal);
        this.comments.forEach(Comment::applyWriterWithdrawal);
    }

    public void validateLoginType(final LoginType loginType) {
        if (this.loginType != loginType) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_LOGIN_TYPE);
        }
    }

    private void validateRoleEach(final Member target) {
        if (this.role != UserRole.MASTER || target.role == UserRole.MASTER) {
            throw new BusinessException(ExceptionCode.INVALID_AUTHORITY);
        }
    }

    public List<Post> getPosts() {
        return Collections.unmodifiableList(this.posts);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(this.comments);
    }

    public String getNickname() {
        return this.profile.getNickname();
    }

    public long getPostCount() {
        return this.posts.size();
    }

    public long getCommentCount() {
        return this.comments.size();
    }
}