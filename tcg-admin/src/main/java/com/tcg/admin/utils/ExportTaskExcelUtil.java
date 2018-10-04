package com.tcg.admin.utils;

import com.tcg.admin.model.Task;
import com.tcg.admin.model.Transaction;
import com.tcg.admin.to.dto.TaskDTO;
import com.tcg.admin.to.dto.TaskTransactionDTO;

import net.sf.json.JSONArray;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

public class ExportTaskExcelUtil {

	private ExportTaskExcelUtil() {}
	
	public static final JSONArray returnTaskArray(Page<Task> result){
		List<TaskDTO> taskDTOList = project(result.getContent(), TaskDTO.class,
				on(Task.class).getTaskId(),
				on(Task.class).getSubSystemTask(),
				on(Task.class).getState().getType(),
				on(Task.class).getStatus(),
				on(Task.class).getCreateOperator(),
				on(Task.class).getCreateTime(),
				on(Task.class).getOpenTime(),
				on(Task.class).getCloseTime(),
				on(Task.class).getOwnerName());
		return JSONArray.fromObject(taskDTOList);
	}

	public static final JSONArray returnTaskTransactionArray(Page<Task> result){
		List<Transaction> transactionList = new ArrayList<>();
		for (Task task : result){
			transactionList.addAll(task.getTransaction());
		}

		List<TaskTransactionDTO> taskDTOList = project(transactionList, TaskTransactionDTO.class,
				on(Transaction.class).getTaskId(),
				on(Transaction.class).getTransactionType(),
				on(Transaction.class).getDescription(),
				on(Transaction.class).getSubSystemTask(),
				on(Transaction.class).getStatus(),
				on(Transaction.class).getStateName(),
				on(Transaction.class).getOwnerName(),
				on(Transaction.class).getCreateTime(),
				on(Transaction.class).getOpenTime(),
				on(Transaction.class).getCloseTime(),
				on(Transaction.class).getUpdateTime(),
				on(Transaction.class).getUpdateOperator()
		);
		return JSONArray.fromObject(taskDTOList);
	}

	public static final ReportTO returnTaskTO(String startDate, String endDate, JSONArray array, String language){
		ReportTO taskReport = new ReportTO();
		String title = "TASK HISTORY ";
		String subTitle = " From " + startDate + " To " + endDate;
		String[] tableTitle;
		String[] tableTitleName = {"taskId", "type", "status", "creator","createTime", "openTime","closeTime","ownerName"};

		switch (language){
			case "en_US":
				tableTitle = new String[]{"Task Id", "Task Type", "Status", "Creator", "Create Time", "Open Time", "Close Time", "Approve"};
				break;
			case "zh_CN":
				tableTitle = new String[]{"流水号", "任务类型", "操作状态", "发起人", "发起时间", "领取时间", "处理完结时间", "审批人"};
				break;
			default:
				tableTitle = new String[]{"流水號", "任務類型", "操作狀態", "發起人", "發起時間", "領取時間", "處理完結時間", "審批人"};
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
				tableTitle = new String[]{"Task Id", "Type", "Description", "Sub System", "Status","Task State","Owner","Create Time ","Open Time","Close Time" ,"Update Date","Updator"};
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
