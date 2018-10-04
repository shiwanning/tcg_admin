package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QBpmGroup is a Querydsl query type for BpmGroup
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QBpmGroup extends EntityPathBase<BpmGroup> {

    private static final long serialVersionUID = 1932455367L;

    public static final QBpmGroup bpmGroup = new QBpmGroup("bpmGroup");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath groupName = createString("groupName");

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QBpmGroup(String variable) {
        super(BpmGroup.class, forVariable(variable));
    }

    public QBpmGroup(Path<? extends BpmGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBpmGroup(PathMetadata<?> metadata) {
        super(BpmGroup.class, metadata);
    }

}

