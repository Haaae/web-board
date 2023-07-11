package toy.board.domain.auth;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCidi is a Querydsl query type for Cidi
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCidi extends EntityPathBase<Cidi> {

    private static final long serialVersionUID = 706326527L;

    public static final QCidi cidi = new QCidi("cidi");

    public final StringPath ci = createString("ci");

    public final StringPath di = createString("di");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QCidi(String variable) {
        super(Cidi.class, forVariable(variable));
    }

    public QCidi(Path<? extends Cidi> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCidi(PathMetadata metadata) {
        super(Cidi.class, metadata);
    }

}

