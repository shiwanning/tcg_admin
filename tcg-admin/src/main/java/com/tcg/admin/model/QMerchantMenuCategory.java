package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QMerchantMenuCategory is a Querydsl query type for MerchantMenuCategory
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMerchantMenuCategory extends EntityPathBase<MerchantMenuCategory> {

    private static final long serialVersionUID = 1036126412L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMerchantMenuCategory merchantMenuCategory = new QMerchantMenuCategory("merchantMenuCategory");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final QMerchantMenuCategorKey key;

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QMerchantMenuCategory(String variable) {
        this(MerchantMenuCategory.class, forVariable(variable), INITS);
    }

    public QMerchantMenuCategory(Path<? extends MerchantMenuCategory> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMerchantMenuCategory(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMerchantMenuCategory(PathMetadata<?> metadata, PathInits inits) {
        this(MerchantMenuCategory.class, metadata, inits);
    }

    public QMerchantMenuCategory(Class<? extends MerchantMenuCategory> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.key = inits.isInitialized("key") ? new QMerchantMenuCategorKey(forProperty("key")) : null;
    }

}

