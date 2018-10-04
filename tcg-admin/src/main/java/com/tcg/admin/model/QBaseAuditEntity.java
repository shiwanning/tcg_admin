package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QBaseAuditEntity is a Querydsl query type for BaseAuditEntity
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QBaseAuditEntity extends EntityPathBase<BaseAuditEntity> {

    private static final long serialVersionUID = -1273000890L;

    public static final QBaseAuditEntity baseAuditEntity = new QBaseAuditEntity("baseAuditEntity");

    public final StringPath createOperatorName = createString("createOperatorName");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final StringPath updateOperatorName = createString("updateOperatorName");

    public final DateTimePath<java.util.Date> updateTime = createDateTime("updateTime", java.util.Date.class);

    public QBaseAuditEntity(String variable) {
        super(BaseAuditEntity.class, forVariable(variable));
    }

    public QBaseAuditEntity(Path<? extends BaseAuditEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseAuditEntity(PathMetadata<?> metadata) {
        super(BaseAuditEntity.class, metadata);
    }

}

