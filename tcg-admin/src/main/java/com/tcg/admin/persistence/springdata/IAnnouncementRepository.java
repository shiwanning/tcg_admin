package com.tcg.admin.persistence.springdata;

import java.util.Date;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.tcg.admin.model.Announcement;

/**
 * Created by ian.r on 6/7/2017.
 */
public interface IAnnouncementRepository extends JpaRepository<Announcement, Integer> {
    @Query("select a from Announcement a where a.announcementId =?1")
    Announcement findByAnnouncementID(Integer announcementId);
    
    @Query("select a from Announcement a where a.announcementType = :announcementType and a.status = 1 and a.startTime < :startDate")
    List<Announcement> findActiveByAnnouncementTypeAndStartDate(@Param("announcementType") String announcementType, @Param("startDate") Date startDate);

    @Query("select an from Announcement an where an.status = 1 order by an.createTime desc")
    @QueryHints(value={@QueryHint(name=org.hibernate.annotations.QueryHints.CACHEABLE, value="true")})
    List<Announcement> findAllAnnouncement();

    @Query("select an from Announcement an "
    		+ " where an.status = 1 "
    		+ " and an.startTime < :endTime "
    		+ " and an.startTime > :startTime "
    		+ " and (an.announcementType = :type or :type = '0') "
    		+ " order by an.startTime desc")
    @Cacheable(cacheNames="tac-announcement", key = "'findByStartTimeGreaterThanAndStatusAndType:@' + T(java.util.Objects).hash(#p0,#p1,#p2)")
    List<Announcement> findByStartTimeGreaterThanAndStatusAndType(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("type") String type);

    @Query("select an from Announcement an "
    		+ " where an.status = 1 "
    		+ " and an.startTime < :endTime "
    		+ " and an.startTime > :startTime "
    		+ " order by an.startTime desc")
    @Cacheable(cacheNames="tac-announcement", key = "'findByStartTimeGreaterThanAndStatus:@' + T(java.util.Objects).hash(#p0,#p1)")
	List<Announcement> findByStartTimeGreaterThanAndStatus(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
}
