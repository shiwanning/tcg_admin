package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QRoleOperator is a Querydsl query type for RoleOperator
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRoleOperator extends EntityPathBase<RoleOperator> {

    private static final long serialVersionUID = 2062515617L;

    public static final QRoleOperator roleOperator = new QRoleOperator("roleOperator");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final NumberPath<Integer> operatorId = createNumber("operatorId", Integer.class);

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final NumberPath<Integer> seqId = createNumber("seqId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QRoleOperator(String variable) {
        super(RoleOperator.class, forVariable(variable));
    }

    public QRoleOperator(Path<? extends RoleOperator> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoleOperator(PathMetadata<?> metadata) {
        super(RoleOperator.class, metadata);
    }

}

