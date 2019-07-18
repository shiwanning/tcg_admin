package com.tcg.admin.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.model.Operator;

/**
 * US共用檢核方法
 */
public class OpUtils {
	
	
	private OpUtils() {
        throw new IllegalStateException("Utility class");
    }
	
    /**
     * </pre> Verify front end user name format: 6~14 alphabet letters or
     * numbers.
     * <p/>
     * 帳號格式(長度6~50，英數字@) </pre>
     *
     * @param userName - the user name to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyUserName(String userName) {
        boolean passed = StringUtils.isNotBlank(userName);
        if (passed) {
            passed = userName.matches("[\\w@\\u4e00-\\u9fa5]{6,50}");
        }
        return passed;
    }

    /**
     * <pre>
     * Verify password format: 6~16 alphabet letters or numbers.
     *
     * 密碼(英數字長度6~16)
     * </pre>
     *
     * @param password - the password to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyPassword(String password) {
        boolean passed = StringUtils.isNotBlank(password);
        if (passed) {
            passed = password.matches("\\w{6,16}");
        }
        return passed;
    }

    /**
     * <pre>
     * Verify operator name format: 6~16 alphabet letters or numbers, starting with "_" or a alphabet letter.
     *
     * 後台管理員帳號格式(長度6~16，英數字, 字母或底線開頭)
     * </pre>
     *
     * @param operatorName - the operator name to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyOperatorName(String operatorName) {
        boolean passed = StringUtils.isNotBlank(operatorName);
        if (passed) {
            Pattern pattern = Pattern.compile("[_a-zA-Z]"); // 開頭規則
            passed = pattern.matcher(operatorName).lookingAt();
        }
        if (passed) {
            passed = operatorName.matches("\\w{6,16}"); // 長度 英數
        }
        return passed;
    }

    /**
     * <pre>
     * Verify password format: 6~16 alphabet letters or numbers.
     *
     * 後台管理員密碼(英數字長度6~16)
     * </pre>
     *
     * @param password - the password to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyOperatorPassword(String password) {
    	return StringUtils.isNotBlank(password) && password.matches("\\w{6,16}");
    }

    /**
     * <pre>
     * Verify operator nickname format: 1~16 alphabet letters or numbers, without white space.
     *
     * 後台管理員暱稱(昵稱不能為空，可全英文或全數字 1-16位 不含空白)
     * </pre>
     *
     * @param nickname - the nickname to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyOperatorNickname(String nickname) {
        boolean passed = StringUtils.isNotBlank(nickname);
        if (passed) {
            passed = nickname.matches("^\\S{1,16}$");
        }
        return passed;
    }

    /**
     * <pre>
     * Verify required fields of the input Operator object.
     * OperatorName, Password, Nickname can not be null.
     *
     * 後台管理員帳號必填欄位
     * </pre>
     *
     * @param operator - the Operator object to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyOperatorFields(Operator operator) {
        boolean passed = StringUtils.isNotBlank(operator.getOperatorName()); // 帳號
        if (passed) {
            passed = StringUtils.isNotBlank(operator.getNickname()); // 暱稱
        }

        return passed;
    }

    /**
     * <pre>
     * Verify whether the flag the same with the login success flag.
     * </pre>
     *
     * @param activeFlag - the active flag to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyActiveFlag(int activeFlag) {
        return activeFlag == LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode();
    }

    /**
     * <pre>
     * Verify nickname format.
     * Nickname can be composed of alphabets, Chinese letters, or numbers.
     * Nickname length should be between 1 and 14.
     * Return false if the format is invalid.
     *
     * 检查会员昵称 由1~14个字符组成,只能输入汉字、字母和数字
     * </pre>
     *
     * @param nickname - the nickname to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyNickname(String nickname) {
        if (StringUtils.isBlank(nickname) || countChineseStringLength(nickname) > 14) {
            return false;
        }

        return nickname.matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+$");
    }

    /**
     * <pre>
     * Verify e-mail address format.
     * User name and web site name can not be empty, and the end of the address part should be more than two letters.
     * Return false if the format is invalid.
     *
     * 检查EMAIL地址 用户名和网站名称必须 >=1 位字符 地址结尾必须是2位以上，如：cn,test,com,info
     * @param email - the e-mail address to verify.
     * @return true if the verify pass, false if verify fail.
     * </pre>
     */
    public static boolean verifyEmail(String email) {
        boolean passed = StringUtils.isNotBlank(email);
        if (passed) {
            String regex1 = "\\w+\\@\\w+\\.\\w{2,}";
            String regex2 = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

            passed = email.matches(regex1) || email.matches(regex2);
        }
        return passed;
    }

    /**
     * <pre>
     * Verify mobile number format.
     * Mobile number should be 11 length of numbers starting with "1".
     * Return false if the format is invalid.
     *
     * 手机号码验证,11位，不知道详细的手机号码段，只是验证开头必须是1和位数
     * </pre>
     *
     * @param mobileNo - the mobile number to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyMobileNo(String mobileNo) {
        return mobileNo.matches("[\\d]{11}");
    }

    /**
     * <pre>
     * Verify Chinese zip code format.
     * Zip code should be 6 length of numbers, and start with "0" is allowed.
     * Return false if the format is invalid.
     *
     * 检查邮政编码(中国),6位，第一位可以是0开头，其他5位数字为0-9
     * </pre>
     *
     * @param zipcode - the zip code to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyZipcode(String zipcode) {
        if (StringUtils.isBlank(zipcode)) {
            return false;
        }

        return zipcode.matches("^[0-9]\\d{5}");
    }

    /**
     * <pre>
     * Verify QQ number format.
     * QQ number should be 5~10 length of numbers, and not start with "0".
     * Return false if the format is invalid.
     *
     * 验证QQ号码 格式：6767676, 号码位数必须是5-10位,头一位不能是"0"
     * </pre>
     *
     * @param qqNo - the QQ number to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyQQCode(String qqNo) {
        if (StringUtils.isBlank(qqNo)) {
            return false;
        }

        return qqNo.matches("^[1-9]\\d{4,11}");
    }

    /**
     * <pre>
     * Verify payee name format.
     * Payee name can be composed of alphabets, English letter “.”, Chinese letters, or Chinese letter “·”
     * Payee name should be less than 32 bytes.
     * Return false if the format is invalid.
     *
     * 检查取款人姓名 长度不能超过32位,只接受中文的汉字与字符“·”、英文的字母和字符“.”
     * </pre>
     *
     * @param payeeName - the payee name to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyPayeeName(String payeeName) {
        if (StringUtils.isBlank(payeeName) || countChineseStringLength(payeeName) > 32) {
            return false;
        }

        return payeeName.matches("^[A-Za-z\\u4e00-\\u9fa5\\.·]+$");
    }

    /**
     * <pre>
     * Verify payment password format.
     * Payment password should be 6~16 letters of alphabet or numbers.
     * Return false if the format is invalid.
     *
     * 检查资金密码 由6到16位数字或字母组成
     * </pre>
     *
     * @param paymentPassword - the payment password to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyPaymentPassword(String paymentPassword) {

        return paymentPassword.matches("\\w{6,16}");
    }

    /**
     * <pre>
     * Count the length of String with Chinese letters.
     * Chinese letters would be counted as 2.
     * </pre>
     *
     * @param str - the string to count length.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static int countChineseStringLength(String str) {
        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).matches("^[\u4E00-\u9FA5]")) {
                length = length + 2;
            } else {
                length = length + 1;
            }
        }

        return length;
    }

    /**
     * <pre>
     * Verify customer group name format.
     * Customer group name should be no longer than 64 letters of alphabet or numbers.
     * Return false if the format is invalid.
     *
     * 检查會員組名稱 超過64字元
     * </pre>
     *
     * @param customerGroupName - the group name to verify.
     *
     * @return true if the verify pass, false if verify fail.
     */
    public static boolean verifyCustomerGroupName(String customerGroupName) {

        if (StringUtils.isBlank(customerGroupName) || countChineseStringLength(customerGroupName) > 64) {
            return false;
        }

        return customerGroupName.matches("^[A-Za-z0-9\\u4e00-\\u9fa5 ]*$");
    }

    public static boolean verifyCustomerParameterName(String customerParameterName) {

        if (StringUtils.isBlank(customerParameterName) || countChineseStringLength(customerParameterName) > 64) {
            return false;
        }

        return customerParameterName.matches("^[A-Za-z0-9\\u4e00-\\u9fa5_ ]*$");
    }

    public static boolean verifyCustomerParameterValue(String customerParameterValue) {

        return !(StringUtils.isBlank(customerParameterValue) || countChineseStringLength(customerParameterValue) > 128);

    }

    public static boolean verifyCustomizePattern(String inputValue, String pattern) {
        if (StringUtils.isBlank(inputValue)) {
            return false;
        }

        return inputValue.matches(pattern);
    }
}
