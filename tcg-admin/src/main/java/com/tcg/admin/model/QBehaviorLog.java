package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QBehaviorLog is a Querydsl query type for BehaviorLog
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QBehaviorLog extends EntityPathBase<BehaviorLog> {

    private static final long serialVersionUID = -208514997L;

    public static final QBehaviorLog behaviorLog = new QBehaviorLog("behaviorLog");

    public final StringPath browser = createString("browser");

    public final DateTimePath<java.util.Date> endProcessDate = createDateTime("endProcessDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final StringPath merchantCode = createString("merchantCode");

    public final StringPath operatorName = createString("operatorName");

    public final StringPath parameters = createString("parameters");

    public final NumberPath<Integer> resourceId = createNumber("resourceId", Integer.class);

    public final NumberPath<Integer> resourceType = createNumber("resourceType", Integer.class);

    public final DateTimePath<java.util.Date> startProcessDate = createDateTime("startProcessDate", java.util.Date.class);

    public final StringPath url = createString("url");

    public QBehaviorLog(String variable) {
        super(BehaviorLog.class, forVariable(variable));
    }

    public QBehaviorLog(Path<? extends BehaviorLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBehaviorLog(PathMetadata<?> metadata) {
        super(BehaviorLog.class, metadata);
    }

}

