package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcg.admin.model.TaskType;

public interface IStateLabelType extends JpaRepository<TaskType, Integer> {

}
