package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QOperatorAuth is a Querydsl query type for OperatorAuth
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOperatorAuth extends EntityPathBase<OperatorAuth> {

    private static final long serialVersionUID = 78040787L;

    public static final QOperatorAuth operatorAuth = new QOperatorAuth("operatorAuth");

    public final QBaseAuditEntity _super = new QBaseAuditEntity(this);

    public final StringPath authKey = createString("authKey");

    public final StringPath authType = createString("authType");

    //inherited
    public final StringPath createOperatorName = _super.createOperatorName;

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final DateTimePath<java.util.Date> lastPassTime = createDateTime("lastPassTime", java.util.Date.class);

    public final NumberPath<Integer> operatorId = createNumber("operatorId", Integer.class);

    public final NumberPath<Long> rid = createNumber("rid", Long.class);

    public final EnumPath<OperatorAuth.Status> status = createEnum("status", OperatorAuth.Status.class);

    //inherited
    public final StringPath updateOperatorName = _super.updateOperatorName;

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    public QOperatorAuth(String variable) {
        super(OperatorAuth.class, forVariable(variable));
    }

    public QOperatorAuth(Path<? extends OperatorAuth> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOperatorAuth(PathMetadata<?> metadata) {
        super(OperatorAuth.class, metadata);
    }

}

