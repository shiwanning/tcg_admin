package com.tcg.admin.controller.subsystem;

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.to.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.cache.MerchantCacheEvict;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.MerchantOperatorService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.to.OperatorsTO;
import com.tcg.admin.to.request.AdmonRoleCreateTo;
import com.tcg.admin.to.request.MerchantCreateTo;
import com.tcg.admin.to.request.SubscribeMerchantTo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/subsystem/merchant-create", produces = MediaType.APPLICATION_JSON_VALUE)
public class MerchantCreateResource {

	@Autowired
	private MerchantService merchantService;

	@PostMapping("")
	@MerchantCacheEvict(allEntries = true)
	public JsonResponse createMerchant(@RequestBody MerchantCreateTo merchantCreateTo) {

		Merchant company = merchantService.findMerchantByMerchantCode(merchantCreateTo.getUpline());
		Boolean createCompany = false;
		if (company == null) {
			createCompany = true;
			company = convertToCompany(merchantCreateTo);
		}

		Merchant merchant = convertToMerchant(merchantCreateTo);

		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		Integer opId = userInfo.getUser().getOperatorId();
		merchant.setCreateOperator(opId);
		company.setCreateOperator(opId);

		merchantService.createMerchant(company, merchant, createCompany);

		return JsonResponseT.OK;
	}
	
	@PostMapping("/create-admin-operator")
	public JsonResponseT<OperatorsTO> createAdminOperator(@RequestBody AdmonRoleCreateTo admonRoleCreateTo) {

		Operator merchantOperator = merchantService.createAdminOperator(admonRoleCreateTo.getMerchantCode(), admonRoleCreateTo.getRoles());
		
		return new JsonResponseT<>(true, new OperatorsTO(merchantOperator.getOperatorId(), merchantOperator.getOperatorName()));
	}
	
	@PostMapping("/subscribe-merchant")
	public JsonResponse subscribeMerchant(@RequestBody SubscribeMerchantTo subscribeMerchantTo) {

		merchantService.subscribeUser(subscribeMerchantTo.getMerchantCode(), subscribeMerchantTo.getOperatorNames(), subscribeMerchantTo.getRoleIds());
		
		return JsonResponse.OK;
	}


	private Merchant convertToCompany(MerchantCreateTo to) {
		Merchant company = new Merchant();
		company.setMerchantCode(to.getUpline());
		company.setMerchantName(to.getCompanyName());
		company.setMerchantId(to.getParentId());
		company.setUsMerchantId(to.getCompanyUsMerchantId());
		company.setStatus(1);
		company.setParentId(0);
		company.setUpline("0");
		company.setMerchantType("9");

		return company;
	}

	private Merchant convertToMerchant(MerchantCreateTo to) {
		Merchant merchant = new Merchant();

		merchant.setMerchantCode(to.getMerchantCode());
		merchant.setMerchantName(to.getMerchantName());
		merchant.setMerchantId(to.getMerchantId());
		merchant.setCurrency(to.getCurrency());
		merchant.setCustomerId(to.getCustomerId().toString());
		merchant.setMerchantType(to.getMerchantType());
		merchant.setStatus(1);
		merchant.setParentId(to.getParentId());
		merchant.setUpline(to.getUpline());
		merchant.setUsMerchantId(to.getUsMerchantId());

		return merchant;
	}

}
