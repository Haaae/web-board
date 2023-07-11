package toy.board.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthentication is a Querydsl query type for Authentication
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthentication extends EntityPathBase<Authentication> {

    private static final long serialVersionUID = 1018616201L;

    public static final QAuthentication authentication = new QAuthentication("authentication");

    public final DateTimePath<java.time.LocalDateTime> authDate = createDateTime("authDate", java.time.LocalDateTime.class);

    public final StringPath birthday = createString("birthday");

    public final StringPath cellPhone = createString("cellPhone");

    public final StringPath email = createString("email");

    public final EnumPath<toy.board.domain.Agree> getherAgree = createEnum("getherAgree", toy.board.domain.Agree.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<Nation> nation = createEnum("nation", Nation.class);

    public final EnumPath<SEX> sex = createEnum("sex", SEX.class);

    public QAuthentication(String variable) {
        super(Authentication.class, forVariable(variable));
    }

    public QAuthentication(Path<? extends Authentication> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthentication(PathMetadata metadata) {
        super(Authentication.class, metadata);
    }

}

