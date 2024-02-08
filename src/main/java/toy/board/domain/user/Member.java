package toy.board.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.ToString;
import toy.board.domain.base.BaseEntity;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.utils.Assert;

@Entity
@lombok.Getter
@lombok.NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {

    public static final int USER_ID_LENGTH = 50;
    public static final int NICKNAME_LENGTH = 8;
    public static final int PASSWORD_LENGTH = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long id;

    /**
     * 로컬 가입의 경우 유저의 인증 이메일.
     */
    @Column(name = "username", length = USER_ID_LENGTH, nullable = false, unique = true)
    private String username;

    @Column(name = "user_role", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "nickname", length = NICKNAME_LENGTH, nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", nullable = false, length = PASSWORD_LENGTH)
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    public Member(
            @NotBlank final String username,
            @NotBlank final String nickname,
            @NotBlank final String password,
            @NotNull final UserRole userRole
    ) {
        validate(username, nickname, password, userRole);

        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.role = userRole;
    }

    private static void validate(
            final String username,
            final String nickname,
            final String password,
            final UserRole userRole
    ) {
        Assert.hasTextAndLength(username, USER_ID_LENGTH);
        Assert.hasTextAndLength(nickname, NICKNAME_LENGTH);
        Assert.hasTextAndLength(password, PASSWORD_LENGTH);
        Assert.notNull(userRole);
    }

    /**
     * target Member의 userRole을 ADMIN으로 승격한다. MASTER MEMBER만 이 메서드를 사용할 수 있다.
     *
     * @param target
     */
    public void updateRole(Member target) {
        validateRoleEach(target);
        target.role = UserRole.ADMIN;
    }

    private void validateRoleEach(final Member target) {
        if (this.role != UserRole.MASTER || target.role == UserRole.MASTER) {
            throw new BusinessException(ExceptionCode.INVALID_AUTHORITY);
        }
    }

    /**
     * Post와의 양방향 매핑을 위함 메서드. Post에서 호출한다.
     *
     * @param post
     */
    public void addPost(final Post post) {
        this.posts.add(post);
    }

    /**
     * Comment와의 양방향 매핑을 위한 메서드. Comment에서 호출한다.
     *
     * @param comment
     */
    public void addComment(final Comment comment) {
        this.comments.add(comment);
    }

    public boolean hasDeleteRight() {
        return role.isDeleteRight();
    }

    /**
     * 유저 탈퇴 시 호출하여 작성했던 게시글과 댓글의 작성자를 null로 만드는 기능
     */
    public void changeAllPostAndCommentWriterToNull() {
        this.posts.forEach(post -> post.applyWriterWithdrawal(this));
        this.comments.forEach(comment -> comment.applyWriterWithdrawal(this));
    }

    public List<Post> getPosts() {
        return Collections.unmodifiableList(this.posts);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(this.comments);
    }

    public long countPosts() {
        return this.posts.size();
    }

    public long countComments() {
        return this.comments.stream()
                .filter(c -> !c.isDeleted())
                .count();
    }
}