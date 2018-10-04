package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMerchantProperties is a Querydsl query type for MerchantProperties
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMerchantProperties extends EntityPathBase<MerchantProperties> {

    private static final long serialVersionUID = -12241342L;

    public static final QMerchantProperties merchantProperties = new QMerchantProperties("merchantProperties");

    public final QBaseAuditEntity _super = new QBaseAuditEntity(this);

    //inherited
    public final StringPath createOperatorName = _super.createOperatorName;

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath merchantCode = createString("merchantCode");

    public final NumberPath<Long> rid = createNumber("rid", Long.class);

    //inherited
    public final StringPath updateOperatorName = _super.updateOperatorName;

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    public final StringPath whitelistFunction = createString("whitelistFunction");

    public QMerchantProperties(String variable) {
        super(MerchantProperties.class, forVariable(variable));
    }

    public QMerchantProperties(Path<? extends MerchantProperties> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMerchantProperties(PathMetadata<?> metadata) {
        super(MerchantProperties.class, metadata);
    }

}

