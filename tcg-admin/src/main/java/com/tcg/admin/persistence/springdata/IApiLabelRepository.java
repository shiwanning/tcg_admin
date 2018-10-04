package com.tcg.admin.persistence.springdata;

import java.util.List;

import com.tcg.admin.model.BpmGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcg.admin.model.ApiLabel;

public interface IApiLabelRepository extends CrudRepository<ApiLabel, String>, JpaRepository<ApiLabel, String> {

	ApiLabel findByIdAndLanguageCode(Integer menuId, String languageCode);

	List<ApiLabel> findById(Integer menuId);

	@Query("SELECT x FROM ApiLabel x WHERE x.id in ?1")
	public List<ApiLabel> findByIds(List<Integer> apiIdList);

}
