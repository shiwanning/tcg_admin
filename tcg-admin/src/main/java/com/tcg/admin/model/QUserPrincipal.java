package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QUserPrincipal is a Querydsl query type for UserPrincipal
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QUserPrincipal extends EntityPathBase<UserPrincipal> {

    private static final long serialVersionUID = 1403862812L;

    public static final QUserPrincipal userPrincipal = new QUserPrincipal("userPrincipal");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> activeFlag = createNumber("activeFlag", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final NumberPath<Integer> errorCount = createNumber("errorCount", Integer.class);

    public final DateTimePath<java.util.Date> errorTime = createDateTime("errorTime", java.util.Date.class);

    public final StringPath password = createString("password");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QUserPrincipal(String variable) {
        super(UserPrincipal.class, forVariable(variable));
    }

    public QUserPrincipal(Path<? extends UserPrincipal> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPrincipal(PathMetadata<?> metadata) {
        super(UserPrincipal.class, metadata);
    }

}

