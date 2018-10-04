package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QMenuItem is a Querydsl query type for MenuItem
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMenuItem extends EntityPathBase<MenuItem> {

    private static final long serialVersionUID = -1474179335L;

    public static final QMenuItem menuItem = new QMenuItem("menuItem");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath accessType = createString("accessType");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final StringPath icon = createString("icon");

    public final NumberPath<Integer> isButton = createNumber("isButton", Integer.class);

    public final NumberPath<Integer> isDisplay = createNumber("isDisplay", Integer.class);

    public final NumberPath<Integer> isLeaf = createNumber("isLeaf", Integer.class);

    public final MapPath<String, ApiLabel, QApiLabel> labels = this.<String, ApiLabel, QApiLabel>createMap("labels", String.class, ApiLabel.class, QApiLabel.class);

    public final ListPath<MenuCategoryMenu, QMenuCategoryMenu> menuCategoryMenu = this.<MenuCategoryMenu, QMenuCategoryMenu>createList("menuCategoryMenu", MenuCategoryMenu.class, QMenuCategoryMenu.class, PathInits.DIRECT2);

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    public final StringPath menuName = createString("menuName");

    public final NumberPath<Integer> menuType = createNumber("menuType", Integer.class);

    public final NumberPath<Integer> parentId = createNumber("parentId", Integer.class);

    public final NumberPath<Integer> treeLevel = createNumber("treeLevel", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    public final StringPath url = createString("url");

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QMenuItem(String variable) {
        super(MenuItem.class, forVariable(variable));
    }

    public QMenuItem(Path<? extends MenuItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuItem(PathMetadata<?> metadata) {
        super(MenuItem.class, metadata);
    }

}

