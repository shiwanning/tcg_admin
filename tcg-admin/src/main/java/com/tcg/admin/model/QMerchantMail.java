package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMerchantMail is a Querydsl query type for MerchantMail
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMerchantMail extends EntityPathBase<MerchantMail> {

    private static final long serialVersionUID = 799434246L;

    public static final QMerchantMail merchantMail = new QMerchantMail("merchantMail");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final BooleanPath isAuth = createBoolean("isAuth");

    public final StringPath merchantCode = createString("merchantCode");

    public final NumberPath<Integer> merchantId = createNumber("merchantId", Integer.class);

    public final StringPath smtpHost = createString("smtpHost");

    public final StringPath smtpPassword = createString("smtpPassword");

    public final StringPath smtpPort = createString("smtpPort");

    public final StringPath smtpUser = createString("smtpUser");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QMerchantMail(String variable) {
        super(MerchantMail.class, forVariable(variable));
    }

    public QMerchantMail(Path<? extends MerchantMail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMerchantMail(PathMetadata<?> metadata) {
        super(MerchantMail.class, metadata);
    }

}

