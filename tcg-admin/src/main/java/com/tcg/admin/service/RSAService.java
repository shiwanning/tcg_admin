package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;

import java.io.Serializable;
import java.security.PrivateKey;

public interface RSAService extends Serializable {

	/**
	 * Get the Generated RSA public key.
	 * 
	 * @return
	 * @throws AdminServiceBaseException
	 */
    String getPublicKey() throws AdminServiceBaseException;

	/**
	 * Get the Generated RSA private key.
	 * 
	 * @param publicKey
	 * @return
	 * @throws AdminServiceBaseException
	 */
    PrivateKey getPrivateKey(String publicKey) throws AdminServiceBaseException;
}
