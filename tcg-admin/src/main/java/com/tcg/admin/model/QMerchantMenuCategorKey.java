package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMerchantMenuCategorKey is a Querydsl query type for MerchantMenuCategorKey
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QMerchantMenuCategorKey extends BeanPath<MerchantMenuCategorKey> {

    private static final long serialVersionUID = -714971694L;

    public static final QMerchantMenuCategorKey merchantMenuCategorKey = new QMerchantMenuCategorKey("merchantMenuCategorKey");

    public final StringPath menuCategoryName = createString("menuCategoryName");

    public final StringPath merchantCode = createString("merchantCode");

    public QMerchantMenuCategorKey(String variable) {
        super(MerchantMenuCategorKey.class, forVariable(variable));
    }

    public QMerchantMenuCategorKey(Path<? extends MerchantMenuCategorKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMerchantMenuCategorKey(PathMetadata<?> metadata) {
        super(MerchantMenuCategorKey.class, metadata);
    }

}

