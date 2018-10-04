package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QSystemHelperTemp is a Querydsl query type for SystemHelperTemp
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSystemHelperTemp extends EntityPathBase<SystemHelperTemp> {

    private static final long serialVersionUID = 698194712L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSystemHelperTemp systemHelperTemp = new QSystemHelperTemp("systemHelperTemp");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath cnContentTemp = createString("cnContentTemp");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath enContentTemp = createString("enContentTemp");

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    public final StringPath processedBy = createString("processedBy");

    public final StringPath remarks = createString("remarks");

    public final StringPath requester = createString("requester");

    public final NumberPath<Integer> requesterId = createNumber("requesterId", Integer.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final QSystemHelper systemHelper;

    public final NumberPath<Integer> taskId = createNumber("taskId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QSystemHelperTemp(String variable) {
        this(SystemHelperTemp.class, forVariable(variable), INITS);
    }

    public QSystemHelperTemp(Path<? extends SystemHelperTemp> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSystemHelperTemp(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSystemHelperTemp(PathMetadata<?> metadata, PathInits inits) {
        this(SystemHelperTemp.class, metadata, inits);
    }

    public QSystemHelperTemp(Class<? extends SystemHelperTemp> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.systemHelper = inits.isInitialized("systemHelper") ? new QSystemHelper(forProperty("systemHelper"), inits.get("systemHelper")) : null;
    }

}

