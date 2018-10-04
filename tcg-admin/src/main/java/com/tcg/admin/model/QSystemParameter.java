package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSystemParameter is a Querydsl query type for SystemParameter
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSystemParameter extends EntityPathBase<SystemParameter> {

    private static final long serialVersionUID = -1958268301L;

    public static final QSystemParameter systemParameter = new QSystemParameter("systemParameter");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath description = createString("description");

    public final StringPath paramName = createString("paramName");

    public final StringPath paramValue = createString("paramValue");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QSystemParameter(String variable) {
        super(SystemParameter.class, forVariable(variable));
    }

    public QSystemParameter(Path<? extends SystemParameter> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSystemParameter(PathMetadata<?> metadata) {
        super(SystemParameter.class, metadata);
    }

}

