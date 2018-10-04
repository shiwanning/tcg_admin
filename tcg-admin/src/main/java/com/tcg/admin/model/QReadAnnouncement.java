package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QReadAnnouncement is a Querydsl query type for ReadAnnouncement
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QReadAnnouncement extends EntityPathBase<ReadAnnouncement> {

    private static final long serialVersionUID = 1067713764L;

    public static final QReadAnnouncement readAnnouncement = new QReadAnnouncement("readAnnouncement");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> announcementId = createNumber("announcementId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final NumberPath<Integer> Id = createNumber("Id", Integer.class);

    public final NumberPath<Integer> operatorId = createNumber("operatorId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QReadAnnouncement(String variable) {
        super(ReadAnnouncement.class, forVariable(variable));
    }

    public QReadAnnouncement(Path<? extends ReadAnnouncement> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReadAnnouncement(PathMetadata<?> metadata) {
        super(ReadAnnouncement.class, metadata);
    }

}

