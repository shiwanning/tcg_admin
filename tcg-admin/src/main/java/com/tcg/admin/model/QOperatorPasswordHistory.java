package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QOperatorPasswordHistory is a Querydsl query type for OperatorPasswordHistory
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOperatorPasswordHistory extends EntityPathBase<OperatorPasswordHistory> {

    private static final long serialVersionUID = 1573708654L;

    public static final QOperatorPasswordHistory operatorPasswordHistory = new QOperatorPasswordHistory("operatorPasswordHistory");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath operatorName = createString("operatorName");

    public final StringPath password = createString("password");

    public final NumberPath<Integer> seqId = createNumber("seqId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QOperatorPasswordHistory(String variable) {
        super(OperatorPasswordHistory.class, forVariable(variable));
    }

    public QOperatorPasswordHistory(Path<? extends OperatorPasswordHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOperatorPasswordHistory(PathMetadata<?> metadata) {
        super(OperatorPasswordHistory.class, metadata);
    }

}

