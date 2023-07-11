package toy.board.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 861807275L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final QAuthentication authentication;

    public final toy.board.domain.auth.QCidi cidi;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final toy.board.domain.auth.QLogin login;

    public final QProfile profile;

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    public final toy.board.domain.auth.QSocialLogin socialLogin;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.authentication = inits.isInitialized("authentication") ? new QAuthentication(forProperty("authentication")) : null;
        this.cidi = inits.isInitialized("cidi") ? new toy.board.domain.auth.QCidi(forProperty("cidi")) : null;
        this.login = inits.isInitialized("login") ? new toy.board.domain.auth.QLogin(forProperty("login")) : null;
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile")) : null;
        this.socialLogin = inits.isInitialized("socialLogin") ? new toy.board.domain.auth.QSocialLogin(forProperty("socialLogin")) : null;
    }

}

