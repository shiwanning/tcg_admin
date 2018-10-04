package com.tcg.admin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;
import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAnnouncement is a Querydsl query type for Announcement
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAnnouncement extends EntityPathBase<Announcement> {

    private static final long serialVersionUID = 335713198L;

    public static final QAnnouncement announcement = new QAnnouncement("announcement");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> announcementId = createNumber("announcementId", Integer.class);

    public final StringPath announcementType = createString("announcementType");

    public final StringPath cnContent = createString("cnContent");

    public final StringPath cnSummary = createString("cnSummary");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final StringPath enContent = createString("enContent");

    public final StringPath enSummary = createString("enSummary");

    public final NumberPath<Integer> frequency = createNumber("frequency", Integer.class);

    public final StringPath merchants = createString("merchants");

    public final NumberPath<Integer> merchantType = createNumber("merchantType", Integer.class);

    public final DateTimePath<java.util.Date> startTime = createDateTime("startTime", java.util.Date.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    public final StringPath vendor = createString("vendor");

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QAnnouncement(String variable) {
        super(Announcement.class, forVariable(variable));
    }

    public QAnnouncement(Path<? extends Announcement> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAnnouncement(PathMetadata<?> metadata) {
        super(Announcement.class, metadata);
    }

}

