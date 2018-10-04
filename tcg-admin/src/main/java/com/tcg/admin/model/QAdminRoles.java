package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAdminRoles is a Querydsl query type for AdminRoles
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAdminRoles extends EntityPathBase<AdminRoles> {

    private static final long serialVersionUID = 2043742421L;

    public static final QAdminRoles adminRoles = new QAdminRoles("adminRoles");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final StringPath roleName = createString("roleName");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QAdminRoles(String variable) {
        super(AdminRoles.class, forVariable(variable));
    }

    public QAdminRoles(Path<? extends AdminRoles> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdminRoles(PathMetadata<?> metadata) {
        super(AdminRoles.class, metadata);
    }

}

