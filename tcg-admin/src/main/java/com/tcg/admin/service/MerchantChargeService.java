package com.tcg.admin.service;

import java.math.BigDecimal;

import com.tcg.admin.to.FileInfoTo;
import com.tcg.admin.to.MerchantProductTo;

public interface MerchantChargeService {

	MerchantProductTo getProductInfo(String merchantCode);

	boolean updateMerchantAccountAmount(String merchantCode, BigDecimal amount, Boolean isCashPledge, String remark,
			FileInfoTo fileInfo);

	boolean cashoutMerchantAccountAmount(String merchantCode, BigDecimal amount, Boolean isCashPledge, String remark);

}
