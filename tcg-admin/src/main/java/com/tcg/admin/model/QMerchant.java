package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMerchant is a Querydsl query type for Merchant
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMerchant extends EntityPathBase<Merchant> {

    private static final long serialVersionUID = -1375380561L;

    public static final QMerchant merchant = new QMerchant("merchant");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> createOperator = createNumber("createOperator", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath currency = createString("currency");

    public final StringPath customerId = createString("customerId");

    public final StringPath merchantCode = createString("merchantCode");

    public final NumberPath<Integer> merchantId = createNumber("merchantId", Integer.class);

    public final StringPath merchantName = createString("merchantName");

    public final StringPath merchantType = createString("merchantType");

    public final NumberPath<Integer> parentId = createNumber("parentId", Integer.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    public final StringPath upline = createString("upline");

    public final NumberPath<Integer> usMerchantId = createNumber("usMerchantId", Integer.class);

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QMerchant(String variable) {
        super(Merchant.class, forVariable(variable));
    }

    public QMerchant(Path<? extends Merchant> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMerchant(PathMetadata<?> metadata) {
        super(Merchant.class, metadata);
    }

}

