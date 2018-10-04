package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QOperator is a Querydsl query type for Operator
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOperator extends EntityPathBase<Operator> {

    private static final long serialVersionUID = -1370637685L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOperator operator = new QOperator("operator");

    public final QUserPrincipal _super = new QUserPrincipal(this);

    //inherited
    public final NumberPath<Integer> activeFlag = _super.activeFlag;

    public final StringPath baseMerchantCode = createString("baseMerchantCode");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    //inherited
    public final NumberPath<Integer> errorCount = _super.errorCount;

    //inherited
    public final DateTimePath<java.util.Date> errorTime = _super.errorTime;

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Integer> operatorId = createNumber("operatorId", Integer.class);

    public final StringPath operatorName = createString("operatorName");

    //inherited
    public final StringPath password = _super.password;

    public final QOperatorProfile profile;

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QOperator(String variable) {
        this(Operator.class, forVariable(variable), INITS);
    }

    public QOperator(Path<? extends Operator> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOperator(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOperator(PathMetadata<?> metadata, PathInits inits) {
        this(Operator.class, metadata, inits);
    }

    public QOperator(Class<? extends Operator> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QOperatorProfile(forProperty("profile")) : null;
    }

}

