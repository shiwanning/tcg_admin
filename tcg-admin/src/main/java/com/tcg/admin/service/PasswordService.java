package com.tcg.admin.service;

import java.io.Serializable;

public interface PasswordService extends Serializable {

    String getPassword(String toke, Integer customerId);

    String getEncryptedPassword(Integer customerId);

}
