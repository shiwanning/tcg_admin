package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcg.admin.model.DomainProperties;

public interface IDomainPropertiesRepository extends JpaRepository<DomainProperties, Long> {

	DomainProperties findByDomain(String domainName);
}
