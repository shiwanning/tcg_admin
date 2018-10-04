package com.tcg.admin.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.model.Announcement;
import com.tcg.admin.model.QAnnouncement;
import com.tcg.admin.utils.DateTools;
import com.tcg.admin.utils.QuerydslPageUtil;

@Repository
public class AnnouncementRepositoryCustom {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    private static final Integer ALL = 0;
    private static final Integer ACTIVE = 1;
    private static final Integer DATE_WITH_TIME_LENGTH = 19;

    public Page<Announcement>  find(String startDate, String endDate, String summaryContent, Integer status, Pageable pageable,String announcementType,String vendor) {
        EntityManager em = entityManager;
        JPAQuery query = new JPAQuery(em);
        QAnnouncement announcement = QAnnouncement.announcement;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(StringUtils.isNotEmpty(startDate)) {
            if(startDate.trim().length() != DATE_WITH_TIME_LENGTH) {
                booleanBuilder.and(announcement.startTime.after(DateTools.toDate("MM/dd/yyyy", startDate)));
            } else {
                booleanBuilder.and(announcement.startTime.after(DateTools.toDate("MM/dd/yyyy hh:mm:ss", startDate)));
            }
        }
        if(StringUtils.isNotEmpty(endDate)) {
            booleanBuilder.and(announcement.startTime.before(DateTools.getNextDate(DateTools.toDate("MM/dd/yyyy",endDate))));
        }
        if(StringUtils.isNotEmpty(summaryContent)) {
            booleanBuilder.and(announcement.enContent.like("%"+summaryContent+"%").or(announcement.enSummary.like("%"+summaryContent+"%"))
                    .or(announcement.cnContent.like("%"+summaryContent+"%").or(announcement.cnSummary.like("%"+summaryContent+"%"))));
        }
        if(!status.equals(ALL)) {
            booleanBuilder.and(announcement.status.eq(status));
        }
        
        if(Integer.parseInt(announcementType) != ALL){
        	booleanBuilder.and(announcement.announcementType.eq(announcementType));
        }
        if(StringUtils.isNotEmpty(vendor)){
            booleanBuilder.and(announcement.vendor.eq(vendor));
        }
        
       /* List<String> list = Arrays.asList("foo", "bar", "waa");
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        org.apache.commons.lang.StringUtils.indexOf(announcement.merchants, new String[]{'d', 'a'});*/
        
        /*List<String> list = Arrays.asList("foo", "bar", "waa");
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        StringUtils.inde
        booleanBuilder.and(announcement.merchants.isEmpty().or(StringUtils.indexOfAny(announcement.merchants, cs)));*/
        query.from(announcement)
                .where(booleanBuilder)
                .orderBy(announcement.startTime.desc());

        return QuerydslPageUtil.pagination(query, announcement, pageable);
    }
}
