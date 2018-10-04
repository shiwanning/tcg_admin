package com.tcg.admin.checker;


import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Preconditions {

	private Preconditions() {
		throw new IllegalStateException("Utility class");
	}
	
    public static <T> T checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(msg));
        }
        return reference;
    }

    public static void checkNotNull(Object obj, RuntimeException e) {
        if (obj == null) {
            throw e;
        }
    }

    public static void checkNotEmpty(String str, RuntimeException e) {
        if ((str == null) || (StringUtils.isEmpty(str))) {
            throw e;
        }
    }

    public static void checkNotEmpty(List list, RuntimeException e) {
        if ((list == null) || (list.isEmpty())) {
            throw e;
        }
    }

    public static void checkNotEqual(Object obj, Object obj2, RuntimeException e) {

        if (!obj.equals(obj2)) {
            throw e;
        }
    }

    public static void checkNotEqual(int i1, int i2, RuntimeException e) {

        if ( i1 != i2) {
            throw e;
        }
    }

    public static void checkNotTrue(boolean expression, RuntimeException e) {

        if (!expression) {
            throw e;
        }
    }

    public static void checkNotTrue(boolean expression, Object errorMessage) {

        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }
}
