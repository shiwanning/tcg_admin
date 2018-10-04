package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMenuCategoryMenu is a Querydsl query type for MenuCategoryMenu
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMenuCategoryMenu extends EntityPathBase<MenuCategoryMenu> {

    private static final long serialVersionUID = 919446883L;

    public static final QMenuCategoryMenu menuCategoryMenu = new QMenuCategoryMenu("menuCategoryMenu");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath menuCategoryName = createString("menuCategoryName");

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QMenuCategoryMenu(String variable) {
        super(MenuCategoryMenu.class, forVariable(variable));
    }

    public QMenuCategoryMenu(Path<? extends MenuCategoryMenu> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuCategoryMenu(PathMetadata<?> metadata) {
        super(MenuCategoryMenu.class, metadata);
    }

}

