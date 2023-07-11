package toy.board.domain.auth;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSocialLogin is a Querydsl query type for SocialLogin
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSocialLogin extends EntityPathBase<SocialLogin> {

    private static final long serialVersionUID = -1704370968L;

    public static final QSocialLogin socialLogin = new QSocialLogin("socialLogin");

    public final StringPath accessToken = createString("accessToken");

    public final StringPath externalId = createString("externalId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<SocialCode> socialCode = createEnum("socialCode", SocialCode.class);

    public final DateTimePath<java.time.LocalDateTime> updateDate = createDateTime("updateDate", java.time.LocalDateTime.class);

    public QSocialLogin(String variable) {
        super(SocialLogin.class, forVariable(variable));
    }

    public QSocialLogin(Path<? extends SocialLogin> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSocialLogin(PathMetadata metadata) {
        super(SocialLogin.class, metadata);
    }

}

