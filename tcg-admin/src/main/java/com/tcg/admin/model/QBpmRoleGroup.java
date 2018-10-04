package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QBpmRoleGroup is a Querydsl query type for BpmRoleGroup
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QBpmRoleGroup extends EntityPathBase<BpmRoleGroup> {

    private static final long serialVersionUID = -1373277455L;

    public static final QBpmRoleGroup bpmRoleGroup = new QBpmRoleGroup("bpmRoleGroup");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath groupName = createString("groupName");

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final NumberPath<Integer> seqId = createNumber("seqId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QBpmRoleGroup(String variable) {
        super(BpmRoleGroup.class, forVariable(variable));
    }

    public QBpmRoleGroup(Path<? extends BpmRoleGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBpmRoleGroup(PathMetadata<?> metadata) {
        super(BpmRoleGroup.class, metadata);
    }

}

