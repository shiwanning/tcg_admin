package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QCategoryRole is a Querydsl query type for CategoryRole
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCategoryRole extends EntityPathBase<CategoryRole> {

    private static final long serialVersionUID = 605112571L;

    public static final QCategoryRole categoryRole = new QCategoryRole("categoryRole");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> categoryId = createNumber("categoryId", Integer.class);

    public final NumberPath<Integer> categoryRoleId = createNumber("categoryRoleId", Integer.class);

    public final StringPath createOperator = createString("createOperator");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final StringPath updateOperator = createString("updateOperator");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QCategoryRole(String variable) {
        super(CategoryRole.class, forVariable(variable));
    }

    public QCategoryRole(Path<? extends CategoryRole> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategoryRole(PathMetadata<?> metadata) {
        super(CategoryRole.class, metadata);
    }

}

