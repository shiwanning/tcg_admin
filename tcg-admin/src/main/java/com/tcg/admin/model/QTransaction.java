package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QTransaction is a Querydsl query type for Transaction
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTransaction extends EntityPathBase<Transaction> {

    private static final long serialVersionUID = 1592828823L;

    public static final QTransaction transaction = new QTransaction("transaction");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DateTimePath<java.util.Date> closeTime = createDateTime("closeTime", java.util.Date.class);

    public final StringPath comments = createString("comments");

    public final StringPath createOperator = createString("createOperator");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> merchantId = createNumber("merchantId", Integer.class);

    public final DateTimePath<java.util.Date> openTime = createDateTime("openTime", java.util.Date.class);

    public final NumberPath<Integer> owner = createNumber("owner", Integer.class);

    public final StringPath ownerName = createString("ownerName");

    public final NumberPath<Integer> stateId = createNumber("stateId", Integer.class);

    public final StringPath stateName = createString("stateName");

    public final StringPath status = createString("status");

    public final StringPath subSystemTask = createString("subSystemTask");

    public final NumberPath<Integer> taskId = createNumber("taskId", Integer.class);

    public final StringPath transactionType = createString("transactionType");

    public final NumberPath<Integer> transId = createNumber("transId", Integer.class);

    public final StringPath updateOperator = createString("updateOperator");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QTransaction(String variable) {
        super(Transaction.class, forVariable(variable));
    }

    public QTransaction(Path<? extends Transaction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTransaction(PathMetadata<?> metadata) {
        super(Transaction.class, metadata);
    }

}

