package com.tcg.admin.to.response;

import java.util.List;

import com.google.common.collect.Lists;

public class TaskCountTo {
	
	private List<StateTo> statesCountInfo = Lists.newLinkedList();
	
	public List<StateTo> getStatesCountInfo() {
		return statesCountInfo;
	}
	
	public void setStatesCountInfo(List<StateTo> statesCountInfo) {
		this.statesCountInfo = statesCountInfo;
	}

	public static class StateTo {
		
		private Integer stateId;
		private Integer count;
		
		public Integer getStateId() {
			return stateId;
		}
		public void setStateId(Integer stateId) {
			this.stateId = stateId;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		
	}
}
