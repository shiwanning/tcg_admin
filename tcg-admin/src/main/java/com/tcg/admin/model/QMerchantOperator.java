package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMerchantOperator is a Querydsl query type for MerchantOperator
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMerchantOperator extends EntityPathBase<MerchantOperator> {

    private static final long serialVersionUID = 1833355635L;

    public static final QMerchantOperator merchantOperator = new QMerchantOperator("merchantOperator");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final NumberPath<Integer> merchantId = createNumber("merchantId", Integer.class);

    public final NumberPath<Integer> operatorId = createNumber("operatorId", Integer.class);

    public final NumberPath<Integer> seqId = createNumber("seqId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QMerchantOperator(String variable) {
        super(MerchantOperator.class, forVariable(variable));
    }

    public QMerchantOperator(Path<? extends MerchantOperator> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMerchantOperator(PathMetadata<?> metadata) {
        super(MerchantOperator.class, metadata);
    }

}

