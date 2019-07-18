package com.tcg.admin.service.security;

import static com.tcg.admin.checker.Preconditions.checkNotEmpty;
import static com.tcg.admin.checker.Preconditions.checkNotEqual;
import static com.tcg.admin.checker.Preconditions.checkNotNull;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.MerchantMail;
import com.tcg.admin.model.Operator;
import com.tcg.admin.persistence.springdata.IMerchantMailRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.service.mail.MailSenderInfo;
import com.tcg.admin.service.mail.SimpleMailSender;
import com.tcg.admin.utils.HashingUtils;
import com.tcg.admin.utils.RandomPasswordGenerator;

@Service
public class OperatorPasswordService {

    public static final String EMAIL_SUBJECT_FIND_DWP = "找回密码";
    public static final String EMAIL_CONTENT_FIND_DWP_TEMPLATE = "找回密码成功，系统把你的密码设置为 %s ,请立即修改 ";
    public static final int MD5 = 1;
    public static final int SHA1 = 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorPasswordService.class);

    @Autowired
    private IOperatorRepository operatorRepository;

    @Autowired
    private SimpleMailSender simpleMailSender;

    @Autowired
    private IMerchantMailRepository merchantMailRepository;

    private RandomPasswordGenerator passwordGenerator = new RandomPasswordGenerator();

    /**
     * 用email取得新登入密碼
     *
     * @param email
     *
     * @throws MailException
     * @throws AdminServiceBaseException
     */
    public Operator resetPasswordByEmail(String userName, String email) {

    	LOGGER.info("Validating require fields for findCustomerPasswordByEmail {} {}", userName, email);

        checkNotEmpty(userName, new AdminServiceBaseException(AdminErrorCode.CUSTOMER_INPUT_FIELD_REQUIRE_ERROR,
                                                              "Customer Username and  Email field is require"));
        checkNotEmpty(email, new AdminServiceBaseException(AdminErrorCode.CUSTOMER_INPUT_FIELD_REQUIRE_ERROR,
                                                           "Customer Username and  Email field is require"));

        Operator operator = operatorRepository.findByOperatorName(userName);

        checkNotNull(operator, new AdminServiceBaseException(AdminErrorCode.CUSTOMER_NOT_EXIST_ERROR, "Customer Username is not found"));
        checkNotEmpty(operator.getProfile().getEmail(),
                      new AdminServiceBaseException(AdminErrorCode.CUSTOMER_EMAIL_NOT_EXIST_ERROR, "Customer Email is not found"));
        checkNotEqual(email, operator.getProfile().getEmail(), new AdminServiceBaseException(AdminErrorCode.CUSTOMER_EMAIL_ISDEFFERENT_ERROR, "Customer Email is " +
                                                                                                                                 "different. "));

        checkCustomerStatus(operator);

        String newPassword = passwordGenerator.makeRandomPassword();

        String context = String.format(EMAIL_CONTENT_FIND_DWP_TEMPLATE, newPassword);
        sendMail(operator, EMAIL_SUBJECT_FIND_DWP, context);

        resetPassword(operator, newPassword);

        return operator;

    }

    private void sendMail(Operator operator, String subject, String context) {

        String system = "SYSTEM";
        MailSenderInfo senderInfo = getMailSenderInfo(system);
        senderInfo.setToAddress(operator.getProfile().getEmail());
        senderInfo.setSubject(subject);
        senderInfo.setContent(context);

        LOGGER.info("Sending new password to Operator's email... {} , from : {}", senderInfo.getToAddress(), senderInfo.getFromAddress());
        try {
            simpleMailSender.sendTextMail(senderInfo);
        } catch (MessagingException e) {
            throw new AdminServiceBaseException(AdminErrorCode.SEND_EMAIL_FAIL, "Send the email fail", e);
        }
    }

    private MailSenderInfo getMailSenderInfo(String merchantCode) {

        MerchantMail merchant = merchantMailRepository.findByMerchantCode(merchantCode);
        checkNotNull(merchant, new AdminServiceBaseException(AdminErrorCode.MERCHANT_SMTP_NOT_EXIST_ERROR, "Not found the  SMTP Settings for the " +
                                                                                                           "merchant"));
        MailSenderInfo mailSenderInfo = new MailSenderInfo();
        mailSenderInfo.setMailServerHost(merchant.getSmtpHost());
        mailSenderInfo.setMailServerPort(merchant.getSmtpPort());
        mailSenderInfo.setFromAddress(merchant.getSmtpUser());
        mailSenderInfo.setValidate(merchant.getAuth());
        mailSenderInfo.setUserName(merchant.getSmtpUser());
        mailSenderInfo.setPassword(merchant.getSmtpPassword());

        return mailSenderInfo;
    }

    private void checkCustomerStatus(Operator operator) {
        int[] forbidden = {LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.getStatusCode(), 
                           LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode(),
                LoginConstant.ACTIVE_FLAG_SYSTEM_LOGIN_PROHIBITED.getStatusCode(),
                LoginConstant.ACTIVE_FLAG_PASSWORD_LOGIN_PROHIBITED.getStatusCode(),
        }; // 狀態禁止使用email找回密碼
        for (int i : forbidden) {
            if (operator.getActiveFlag() == i) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "Operator has been forbidden");
            }
        }
    }

    private String resetPassword(Operator operator, String plainPassword) {

        operator.setPassword(hash(MD5, plainPassword));
        operator.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
        operator.setErrorCount(0);
        operator.setGoogleErrorCount(0);
        operatorRepository.save(operator);
        return plainPassword;
    }

    private String hash(int hashAlgorithm, String password) {

        String hash;
        if (hashAlgorithm == SHA1) {
            hash = HashingUtils.encryptBySHA1(password);
        } else {
            hash = HashingUtils.encryptByMD5(password);
        }
        return hash;
    }

}
