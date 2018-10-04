package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMenuItemAux is a Querydsl query type for MenuItemAux
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMenuItemAux extends EntityPathBase<MenuItemAux> {

    private static final long serialVersionUID = -1235901173L;

    public static final QMenuItemAux menuItemAux = new QMenuItemAux("menuItemAux");

    public final NumberPath<Integer> apiFamily = createNumber("apiFamily", Integer.class);

    public final NumberPath<Integer> apiType = createNumber("apiType", Integer.class);

    public final NumberPath<Integer> menuId = createNumber("menuId", Integer.class);

    public QMenuItemAux(String variable) {
        super(MenuItemAux.class, forVariable(variable));
    }

    public QMenuItemAux(Path<? extends MenuItemAux> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuItemAux(PathMetadata<?> metadata) {
        super(MenuItemAux.class, metadata);
    }

}

