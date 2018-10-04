package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMerchantWhitelistIp is a Querydsl query type for MerchantWhitelistIp
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMerchantWhitelistIp extends EntityPathBase<MerchantWhitelistIp> {

    private static final long serialVersionUID = -1948595041L;

    public static final QMerchantWhitelistIp merchantWhitelistIp = new QMerchantWhitelistIp("merchantWhitelistIp");

    public final QBaseAuditEntity _super = new QBaseAuditEntity(this);

    //inherited
    public final StringPath createOperatorName = _super.createOperatorName;

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath ip = createString("ip");

    public final StringPath merchantCode = createString("merchantCode");

    public final NumberPath<Long> rid = createNumber("rid", Long.class);

    //inherited
    public final StringPath updateOperatorName = _super.updateOperatorName;

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    public QMerchantWhitelistIp(String variable) {
        super(MerchantWhitelistIp.class, forVariable(variable));
    }

    public QMerchantWhitelistIp(Path<? extends MerchantWhitelistIp> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMerchantWhitelistIp(PathMetadata<?> metadata) {
        super(MerchantWhitelistIp.class, metadata);
    }

}

