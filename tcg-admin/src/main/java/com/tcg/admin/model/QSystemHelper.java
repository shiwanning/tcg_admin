package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QSystemHelper is a Querydsl query type for SystemHelper
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSystemHelper extends EntityPathBase<SystemHelper> {

    private static final long serialVersionUID = -838003420L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSystemHelper systemHelper = new QSystemHelper("systemHelper");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath cnContent = createString("cnContent");

    public final NumberPath<Integer> createOperator = createNumber("createOperator", Integer.class);

    public final StringPath createOperatorName = createString("createOperatorName");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath enContent = createString("enContent");

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    public final QMenuItem menuItem;

    public final NumberPath<Integer> state = createNumber("state", Integer.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final NumberPath<Integer> updateOperator = createNumber("updateOperator", Integer.class);

    public final StringPath updateOperatorName = createString("updateOperatorName");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QSystemHelper(String variable) {
        this(SystemHelper.class, forVariable(variable), INITS);
    }

    public QSystemHelper(Path<? extends SystemHelper> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSystemHelper(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSystemHelper(PathMetadata<?> metadata, PathInits inits) {
        this(SystemHelper.class, metadata, inits);
    }

    public QSystemHelper(Class<? extends SystemHelper> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.menuItem = inits.isInitialized("menuItem") ? new QMenuItem(forProperty("menuItem")) : null;
    }

}

