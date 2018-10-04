package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QLobbyAccountType is a Querydsl query type for LobbyAccountType
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLobbyAccountType extends EntityPathBase<LobbyAccountType> {

    private static final long serialVersionUID = -1535077320L;

    public static final QLobbyAccountType lobbyAccountType = new QLobbyAccountType("lobbyAccountType");

    public final NumberPath<Integer> accountTypeId = createNumber("accountTypeId", Integer.class);

    public final StringPath accountTypeName = createString("accountTypeName");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final StringPath firmName = createString("firmName");

    public final StringPath hasElott = createString("hasElott");

    public final StringPath hasFish = createString("hasFish");

    public final StringPath hasLive = createString("hasLive");

    public final StringPath hasLott = createString("hasLott");

    public final StringPath hasPvp = createString("hasPvp");

    public final StringPath hasRng = createString("hasRng");

    public final StringPath hasSports = createString("hasSports");

    public final NumberPath<Integer> isTcg = createNumber("isTcg", Integer.class);

    public final NumberPath<Integer> levelId = createNumber("levelId", Integer.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final StringPath supCurrency = createString("supCurrency");

    public final DateTimePath<java.util.Date> updateTime = createDateTime("updateTime", java.util.Date.class);

    public QLobbyAccountType(String variable) {
        super(LobbyAccountType.class, forVariable(variable));
    }

    public QLobbyAccountType(Path<? extends LobbyAccountType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLobbyAccountType(PathMetadata<?> metadata) {
        super(LobbyAccountType.class, metadata);
    }

}

