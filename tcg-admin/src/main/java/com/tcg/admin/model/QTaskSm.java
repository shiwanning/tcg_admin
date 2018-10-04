package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTaskSm is a Querydsl query type for TaskSm
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaskSm extends EntityPathBase<TaskSm> {

    private static final long serialVersionUID = 459002470L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaskSm taskSm = new QTaskSm("taskSm");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DateTimePath<java.util.Date> closeTime = createDateTime("closeTime", java.util.Date.class);

    public final StringPath createOperator = createString("createOperator");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> merchantId = createNumber("merchantId", Integer.class);

    public final DateTimePath<java.util.Date> openTime = createDateTime("openTime", java.util.Date.class);

    public final NumberPath<Integer> owner = createNumber("owner", Integer.class);

    public final StringPath ownerName = createString("ownerName");

    public final QState state;

    public final NumberPath<Integer> stateId = createNumber("stateId", Integer.class);

    public final StringPath status = createString("status");

    public final StringPath subSystemTask = createString("subSystemTask");

    public final NumberPath<Integer> taskId = createNumber("taskId", Integer.class);

    public final StringPath updateOperator = createString("updateOperator");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QTaskSm(String variable) {
        this(TaskSm.class, forVariable(variable), INITS);
    }

    public QTaskSm(Path<? extends TaskSm> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskSm(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskSm(PathMetadata<?> metadata, PathInits inits) {
        this(TaskSm.class, metadata, inits);
    }

    public QTaskSm(Class<? extends TaskSm> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.state = inits.isInitialized("state") ? new QState(forProperty("state")) : null;
    }

}

