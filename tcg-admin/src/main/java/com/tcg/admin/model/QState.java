package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QState is a Querydsl query type for State
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QState extends EntityPathBase<State> {

    private static final long serialVersionUID = -816851958L;

    public static final QState state = new QState("state");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    public final NumberPath<Integer> parentId = createNumber("parentId", Integer.class);

    public final NumberPath<Integer> stateId = createNumber("stateId", Integer.class);

    public final StringPath stateName = createString("stateName");

    public final StringPath type = createString("type");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public final StringPath viewUrl = createString("viewUrl");

    public QState(String variable) {
        super(State.class, forVariable(variable));
    }

    public QState(Path<? extends State> path) {
        super(path.getType(), path.getMetadata());
    }

    public QState(PathMetadata<?> metadata) {
        super(State.class, metadata);
    }

}

