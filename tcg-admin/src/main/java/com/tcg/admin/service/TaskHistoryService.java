package com.tcg.admin.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.tcg.admin.model.State;
import com.tcg.admin.model.Task;
import com.tcg.admin.to.TaskQueryTO;

public interface TaskHistoryService {

	Page<Task> getAll(TaskQueryTO taskQueryTO);

	List<State> getStateList(String type);

}
