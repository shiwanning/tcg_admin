package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTask is a Querydsl query type for Task
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTask extends EntityPathBase<Task> {

    private static final long serialVersionUID = 1497682668L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTask task = new QTask("task");

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

    public final ListPath<Transaction, QTransaction> transaction = this.<Transaction, QTransaction>createList("transaction", Transaction.class, QTransaction.class, PathInits.DIRECT2);

    public final StringPath updateOperator = createString("updateOperator");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QTask(String variable) {
        this(Task.class, forVariable(variable), INITS);
    }

    public QTask(Path<? extends Task> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTask(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTask(PathMetadata<?> metadata, PathInits inits) {
        this(Task.class, metadata, inits);
    }

    public QTask(Class<? extends Task> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.state = inits.isInitialized("state") ? new QState(forProperty("state")) : null;
    }

}

