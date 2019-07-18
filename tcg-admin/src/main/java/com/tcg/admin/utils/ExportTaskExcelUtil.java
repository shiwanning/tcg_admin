package com.tcg.admin.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.google.common.collect.Lists;
import com.tcg.admin.model.Task;
import com.tcg.admin.model.Transaction;
import com.tcg.admin.to.dto.TaskDto;
import com.tcg.admin.to.dto.TaskTransactionDTO;

import net.sf.json.JSONArray;

public class ExportTaskExcelUtil {

	private ExportTaskExcelUtil() {}
	
	public static final JSONArray returnTaskArray(Page<Task> result){
		List<TaskDto> taskDtoList = Lists.newLinkedList();
		for(Task task : result.getContent()) {
			taskDtoList.add(new TaskDto(task));
		}
		return JSONArray.fromObject(taskDtoList);
	}

	public static final JSONArray returnTaskTransactionArray(Page<Task> result){
		List<Transaction> transactionList = new ArrayList<>();
		for (Task task : result){
			transactionList.addAll(task.getTransaction());
		}

		List<TaskTransactionDTO> taskDtoList = Lists.newLinkedList();
		for (Transaction tra : transactionList) {
			taskDtoList.add(new TaskTransactionDTO(tra));
		}
		return JSONArray.fromObject(taskDtoList);
	}

	public static final ReportTO returnTaskTO(String startDate, String endDate, JSONArray array, String language){
		ReportTO taskReport = new ReportTO();
		String title = "TASK HISTORY ";
		String subTitle = " From " + startDate + " To " + endDate;
		String[] tableTitle;
		String[] tableTitleName = {"taskId", "description", "subSystemTask", "status", "creator", "ownerName","createTime", "openTime","closeTime"};

		switch (language){
			case "en_US":
				tableTitle = new String[]{"Task Id", "Description", "Sub System", "Status", "Creator", "Owner", "Create Time", "Open Time", "Close Time"};
				break;
			case "zh_CN":
				tableTitle = new String[]{"流水号", "描述", "任务号", "操作状态", "发起人", "执行人", "发起时间", "领取时间", "处理完结时间"};
				break;
			default:
				tableTitle = new String[]{"流水號", "描述", "任務號", "操作狀態", "發起人", "執行人", "發起時間", "領取時間", "處理完結時間"};
				break;
		}

		taskReport.setTitle(title);
		taskReport.setTableTile(tableTitle);
		taskReport.setAttrName(tableTitleName);
		taskReport.setArray(array);
		taskReport.setSubTitle(subTitle);
		return taskReport;
	}

	public static final ReportTO returnTransactionTO(String startDate, String endDate,JSONArray array,String language){
		ReportTO taskReport = new ReportTO();
		String title = "TASK TRANSACTION HISTORY ";
		String subTitle = " From " + startDate + " To " + endDate;
		String[] tableTitle;
		String[] tableTitleName = {"taskId", "transactionType", "description", "subSystemTask","status", "stateName","ownerName","createTime","openTime","closeTime","updateTime","updator"};

		switch (language){
			case "zh_CN":
				tableTitle = new String[]{"流水号","类型","备注","任务号","操作状态","任务状态","	执行人","任务生成时间	","任务开始时间","任务结束时间","更新日期","更新者"};
				break;
			case "en_US":
				tableTitle = new String[]{"Task Id", "Type", "Description", "Sub System", "Status","State Name","Owner","Create Time ","Open Time","Close Time" ,"Update Time","Updated Operator"};
				break;
			default:
				tableTitle = new String[]{"流水號","類型","備註","任務號","操作狀態","任務狀態"," 執行人","任務生成時間 ","任務開始時間","任務結束時間" ,"更新日期","更新者"};
				break;
		}

		taskReport.setTitle(title);
		taskReport.setTableTile(tableTitle);
		taskReport.setAttrName(tableTitleName);
		taskReport.setArray(array);
		taskReport.setSubTitle(subTitle);
		return taskReport;
	}
}
