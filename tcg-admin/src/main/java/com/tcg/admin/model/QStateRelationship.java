package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QStateRelationship is a Querydsl query type for StateRelationship
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStateRelationship extends EntityPathBase<StateRelationship> {

    private static final long serialVersionUID = 494919522L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStateRelationship stateRelationship = new QStateRelationship("stateRelationship");

    public final NumberPath<Integer> fromState = createNumber("fromState", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QState state;

    public final NumberPath<Integer> toState = createNumber("toState", Integer.class);

    public QStateRelationship(String variable) {
        this(StateRelationship.class, forVariable(variable), INITS);
    }

    public QStateRelationship(Path<? extends StateRelationship> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStateRelationship(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStateRelationship(PathMetadata<?> metadata, PathInits inits) {
        this(StateRelationship.class, metadata, inits);
    }

    public QStateRelationship(Class<? extends StateRelationship> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.state = inits.isInitialized("state") ? new QState(forProperty("state")) : null;
    }

}

