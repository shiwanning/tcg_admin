package com.tcg.admin.persistence.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tcg.admin.model.MerchantMenuCategory;

public interface IMerchantMenuCategoryRepository extends JpaRepository<MerchantMenuCategory, Integer> {

	@Query("select mc.key.menuCategoryName from MerchantMenuCategory mc where mc.key.merchantCode = ?1")
	List<String> findMerchantCategory(String merchantCode);

	@Query("select mc.key.menuCategoryName from MerchantMenuCategory mc where mc.key.merchantCode = ?1 and mc.key.menuCategoryName != 'SYSTEM' ")
	List<String> findMerchantCategoryWithOutSystem(String merchantCode);

	@Query("select mc from MerchantMenuCategory mc where mc.key.merchantCode = ?1")
	List<MerchantMenuCategory> findMerchantMenuCategoryByMerchantCode(String merchantCode);

	@Query("select mc from MerchantMenuCategory mc where mc.key.menuCategoryName = ?1")
	List<MerchantMenuCategory> findMerchantMenuCategoryByMenuCategoryName(String menuCategoryName);

}
