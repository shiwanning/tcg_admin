package com.tcg.admin.service;

import com.tcg.admin.to.VerificationData;

public interface VerificationService {
	
	boolean basic(VerificationData verificationData);

	boolean advanced(VerificationData verificationData);

	boolean menuRecord(VerificationData verificationData);

	boolean task(VerificationData verificationData);
	
	boolean byResourceUrl(VerificationData verificationData);

	boolean advancedByResourceUrl(VerificationData verificationData);

    boolean merchant(VerificationData verificationData);

}
