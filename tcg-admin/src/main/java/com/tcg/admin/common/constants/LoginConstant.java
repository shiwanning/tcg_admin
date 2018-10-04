package com.tcg.admin.common.constants;

import java.util.HashMap;

public enum LoginConstant {

    /**
     * 0: Manual inactive (by Operator).
     */
	ACTIVE_FLAG_LOGIN_FORBID(0),

    /**
     * 1: Allow Login.
     */
	ACTIVE_FLAG_LOGIN_SUCCESS(1),

    /**
     * 2: Login forbidden (login error exceed).
     */
	ACTIVE_FLAG_TERMINATE_LOGIN(2),

    /**
     * 7: User has been deleted.
     * 7: 用戶已被刪除
     */
	ACTIVE_FLAG_DELETE(7),

    /**
     * 10: InActive State (不活躍用戶)
     */
	ACTIVE_FLAG_ACTIVE(10),

    /**
     * 10: InActive State (不活躍用戶With Balance)
     */
	ACTIVE_FLAG_ACTIVE_WITH_BALANCE(11);


    private static final HashMap<Integer, LoginConstant> _STORE = new HashMap<>();

    static {
        for (LoginConstant loginStatus : LoginConstant.values()) {
            _STORE.put(loginStatus.getStatusCode(), loginStatus);
        }
    }

    private int statusCode;

    LoginConstant(int type) {
        this.statusCode = type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 
     * @param statusCode
     * @return
     */
    public static LoginConstant getByStatusCode(int statusCode) {
        return _STORE.get(statusCode);
    }

}
