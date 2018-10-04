package com.tcg.admin.persistence.springdata;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import com.tcg.admin.model.ReadAnnouncement;


public interface IAnnouncementReadRepository extends JpaRepository<ReadAnnouncement, Integer> {
	 @Query("select ra from ReadAnnouncement ra where ra.operatorId =?1")
     @QueryHints(value={@QueryHint(name=org.hibernate.annotations.QueryHints.CACHEABLE, value="true")})
	 List<ReadAnnouncement> findReadAnnouncementByOperatorId(Integer operatorId);

}
	