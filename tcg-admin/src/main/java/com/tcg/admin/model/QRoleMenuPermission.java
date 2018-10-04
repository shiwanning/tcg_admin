package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QRoleMenuPermission is a Querydsl query type for RoleMenuPermission
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRoleMenuPermission extends EntityPathBase<RoleMenuPermission> {

    private static final long serialVersionUID = -68046901L;

    public static final QRoleMenuPermission roleMenuPermission = new QRoleMenuPermission("roleMenuPermission");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final NumberPath<Integer> seqId = createNumber("seqId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QRoleMenuPermission(String variable) {
        super(RoleMenuPermission.class, forVariable(variable));
    }

    public QRoleMenuPermission(Path<? extends RoleMenuPermission> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoleMenuPermission(PathMetadata<?> metadata) {
        super(RoleMenuPermission.class, metadata);
    }

}

