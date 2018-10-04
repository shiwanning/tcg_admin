package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QTaskType is a Querydsl query type for TaskType
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaskType extends EntityPathBase<TaskType> {

    private static final long serialVersionUID = -1280212922L;

    public static final QTaskType taskType = new QTaskType("taskType");

    public final StringPath description = createString("description");

    public final StringPath label = createString("label");

    public final StringPath langCode = createString("langCode");

    public final StringPath typeCode = createString("typeCode");

    public QTaskType(String variable) {
        super(TaskType.class, forVariable(variable));
    }

    public QTaskType(Path<? extends TaskType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTaskType(PathMetadata<?> metadata) {
        super(TaskType.class, metadata);
    }

}

