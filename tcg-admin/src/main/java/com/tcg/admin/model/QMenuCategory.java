package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QMenuCategory is a Querydsl query type for MenuCategory
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMenuCategory extends EntityPathBase<MenuCategory> {

    private static final long serialVersionUID = -784683548L;

    public static final QMenuCategory menuCategory = new QMenuCategory("menuCategory");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath categoryName = createString("categoryName");

    public final StringPath createOperator = createString("createOperator");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final ListPath<MenuCategoryMenu, QMenuCategoryMenu> menuCategoryMenu = this.<MenuCategoryMenu, QMenuCategoryMenu>createList("menuCategoryMenu", MenuCategoryMenu.class, QMenuCategoryMenu.class, PathInits.DIRECT2);

    public final SetPath<MerchantMenuCategory, QMerchantMenuCategory> merchantMenuCategories = this.<MerchantMenuCategory, QMerchantMenuCategory>createSet("merchantMenuCategories", MerchantMenuCategory.class, QMerchantMenuCategory.class, PathInits.DIRECT2);

    public final StringPath updateOperator = createString("updateOperator");

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QMenuCategory(String variable) {
        super(MenuCategory.class, forVariable(variable));
    }

    public QMenuCategory(Path<? extends MenuCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuCategory(PathMetadata<?> metadata) {
        super(MenuCategory.class, metadata);
    }

}

