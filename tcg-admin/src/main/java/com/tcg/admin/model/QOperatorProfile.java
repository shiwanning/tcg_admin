package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QOperatorProfile is a Querydsl query type for OperatorProfile
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOperatorProfile extends EntityPathBase<OperatorProfile> {

    private static final long serialVersionUID = 1672971422L;

    public static final QOperatorProfile operatorProfile = new QOperatorProfile("operatorProfile");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath email = createString("email");

    public final StringPath lastLoginIP = createString("lastLoginIP");

    public final DateTimePath<java.util.Date> lastLoginTime = createDateTime("lastLoginTime", java.util.Date.class);

    public final DateTimePath<java.util.Date> lastLogoutTime = createDateTime("lastLogoutTime", java.util.Date.class);

    public final BooleanPath login = createBoolean("login");

    public final StringPath mobileNo = createString("mobileNo");

    public final NumberPath<Integer> noActive = createNumber("noActive", Integer.class);

    public final BooleanPath notiSound = createBoolean("notiSound");

    public final NumberPath<Integer> operatorId = createNumber("operatorId", Integer.class);

    public final NumberPath<Integer> pageSize = createNumber("pageSize", Integer.class);

    public final DateTimePath<java.util.Date> passwdLastModifyDate = createDateTime("passwdLastModifyDate", java.util.Date.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QOperatorProfile(String variable) {
        super(OperatorProfile.class, forVariable(variable));
    }

    public QOperatorProfile(Path<? extends OperatorProfile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOperatorProfile(PathMetadata<?> metadata) {
        super(OperatorProfile.class, metadata);
    }

}

