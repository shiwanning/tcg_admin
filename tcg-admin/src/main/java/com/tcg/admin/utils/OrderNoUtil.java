package com.tcg.admin.utils;

import java.util.Date;

public class OrderNoUtil {

    public static String getOrderNo(OrderTypeEnum type){
       return  type.toString() + new Date().getTime();
    }

    public enum OrderTypeEnum {
        TCG("TCG")
        ;

        private final String type;

        OrderTypeEnum(String type) {
            this.type = type;
        }

        @Override
        public String toString(){
            return this.type;
        }
    }

}
