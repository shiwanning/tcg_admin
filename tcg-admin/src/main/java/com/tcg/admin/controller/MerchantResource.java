package com.tcg.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Task;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.impl.IMService;
import com.tcg.admin.to.OperatorCreateTO;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.to.response.MerchantListTo;
import com.tcg.admin.utils.StringTools;

@RestController
@RequestMapping(value = "/resources/merchants", produces = MediaType.APPLICATION_JSON_VALUE)
public class MerchantResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantResource.class);

    @Autowired
	private MerchantService merchantService;

    @Autowired
    private OperatorAuthenticationService operatorAuthService;
	
    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;
	
    @Autowired
	IMService imService;

    @GetMapping("/getMerchantList")
	public JsonResponseT<List<Merchant>> getMerchantList() {
		JsonResponseT<List<Merchant>> response = new JsonResponseT<>(true);
		List<Merchant> merchatList = merchantService.getMerchantList();
		response.setValue(merchatList);
		return response;
	}

	/**
	 * Assign operator to merchant, for role management.
	 *
	 * 指派operator到特定品牌
	 */
    @PutMapping("/{operatorName}/_assign_to_merchant")
	public JsonResponse assignToMerchant(
	        @PathVariable("operatorName") String operatorName, 
	        @RequestBody OperatorCreateTO operatorCreateTO) {
		merchantService.assignOperatorMerchants(operatorName, operatorCreateTO.getMerchantIdList());
		return new JsonResponse(true);
	}

	/**
	 * 复制某一存在用户
	 * 根据已存在的帐号进行复制，要求修改名称
	 * 被复制的用户必须与原用户有相同的角色权限功能
	 */
	@PostMapping("/{originOperatorId}/{newOperatorName}/_copy_operator")
	public Operator copyOperator(@PathVariable("originOperatorId") Integer originOperatorId, @PathVariable("newOperatorName") String newOperatorName) {
		return merchantService.copyOperator(originOperatorId, newOperatorName);
	}

	/**
	 * 刪除品牌
	 */
	@DeleteMapping("/{merchatId}")
	public JsonResponse deleteMerchant(@PathVariable("merchatId") Integer merchatId) {
		merchantService.deleteMerchant(merchatId);
		return new JsonResponse(true);
	}

	// *TCGAdmin: 1.creater operator(admin) 2.subscribe merchant to operator 3.subscribe to "system" roles 4.closed task
	@PutMapping("/approve_merchant")
	public JsonResponseT<Boolean> approveMerchant(@RequestBody OperatorCreateTO operatorCreateTO) {
		List<Integer> operatorIdList = new ArrayList<>();
		List<Integer> roleIdList = new ArrayList<>();

		JsonResponseT<Boolean> response = new JsonResponseT<>(true);
		if(!StringTools.isEmptyOrNull(operatorCreateTO.getRoleIds()) && StringTools.isNotBlank(operatorCreateTO.getRoleIds())) {
			roleIdList = StringTools.transStringToList(operatorCreateTO.getRoleIds());
		}
		if(!StringTools.isEmptyOrNull(operatorCreateTO.getOperatorIds()) && StringTools.isNotBlank(operatorCreateTO.getOperatorIds())) {
			operatorIdList =StringTools.transStringToList(operatorCreateTO.getOperatorIds());
		}
		merchantService.approveMerchant(operatorCreateTO, Integer.parseInt(operatorCreateTO.getTaskId()), operatorIdList, roleIdList, operatorCreateTO.getOperatorName());
		response.setValue(true);
		uploadChanges(Integer.parseInt(operatorCreateTO.getTaskId()));

		return response;
	}

	@PostMapping
	public JsonResponseT<Task> createMerchant(@RequestBody OperatorCreateTO operatorCreateTO) {
		JsonResponseT<Task> response = new JsonResponseT<>(true);
		Task task = merchantService.createMerchant(operatorCreateTO.getMerchantId(),operatorCreateTO.getOperatorName(),operatorCreateTO.getMerchantName(),operatorCreateTO.getBaseMerchantCode());
		response.setValue(task);
		uploadChanges(task.getTaskId());
		return response;
	}

	@PostMapping("/reApprove_merchant")
	public JsonResponseT<Task> reApproveMerchant(@RequestBody OperatorCreateTO operatorCreateTO) {
		JsonResponseT<Task> response = new JsonResponseT<>(true);
		Task task = merchantService.reApprove(operatorCreateTO);
		response.setValue(task);
		uploadChanges(task.getTaskId());
		return response;
	}

	@PutMapping("/updateMerchant")
	public JsonResponseT<Boolean> updateMerchant(@RequestBody OperatorCreateTO operatorCreateTO) {
		JsonResponseT<Boolean> response = new JsonResponseT<>(true);
		merchantService.updateMerchant(operatorCreateTO);
		response.setValue(true);
		return response;
	}

	@GetMapping("/getMerchantByType")
	public JsonResponseT<Object> getMerchantByType(@RequestParam(value = "merchantTypeId", required = false)String merchantTypeId) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);

		response.setValue(merchantService.getMerchantByType(merchantTypeId));
		return response;
	}

	@GetMapping("/getProductInfo")
	public JsonResponseT<Map<String, Object>> getProductInfo(@RequestParam(value = "merchantCode", required = false)String baseMerchantCode) {
		JsonResponseT<Map<String, Object>> response = new JsonResponseT<>(true);
		response.setValue(merchantService.getProductInfo(baseMerchantCode));
		return response;
	}

	@PutMapping("/updateProductInfo")
	public JsonResponseT<Boolean> updateProductInfo(@RequestBody OperatorCreateTO operatorCreateTO) {
		JsonResponseT<Boolean> response = new JsonResponseT<>(true);
		merchantService.updateProductInfo(operatorCreateTO);
		response.setValue(true);
		return response;
	}
	
   @GetMapping("/getMerchants")
   public JsonResponseT<MerchantListTo> getMerchants() {
       JsonResponseT<MerchantListTo> response = new JsonResponseT<>(true);
       List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(operatorAuthService.getOperatorByToken(RequestHelper.getToken()));
       
       response.setValue(new MerchantListTo(merchList));
       return response;
   }
   
   @GetMapping("/getAllMerchants")
   public JsonResponseT<MerchantListTo> getAllMerchants() {
       JsonResponseT<MerchantListTo> response = new JsonResponseT<>(true);
       List<Map<String,String>> merchList = roleMenuPermissionService.getAllMerchant();
       
       response.setValue(new MerchantListTo(merchList));
       return response;
   }
   
   private void uploadChanges(Integer taskId) {
	   try {
			imService.uploadChanges(taskId, true);
		} catch (Exception e) {
		    LOGGER.warn("ERROR IN IM SERVICE", e);
		}
   }

}
