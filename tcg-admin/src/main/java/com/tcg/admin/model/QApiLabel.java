package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QApiLabel is a Querydsl query type for ApiLabel
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QApiLabel extends EntityPathBase<ApiLabel> {

    private static final long serialVersionUID = 79226593L;

    public static final QApiLabel apiLabel = new QApiLabel("apiLabel");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath label = createString("label");

    public final StringPath languageCode = createString("languageCode");

    public QApiLabel(String variable) {
        super(ApiLabel.class, forVariable(variable));
    }

    public QApiLabel(Path<? extends ApiLabel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApiLabel(PathMetadata<?> metadata) {
        super(ApiLabel.class, metadata);
    }

}

