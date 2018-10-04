package com.tcg.admin.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 */
@Embeddable
public class MerchantMenuCategorKey implements Serializable {


    public MerchantMenuCategorKey(String merchantCode) {
        super();
        this.merchantCode = merchantCode;
    }

    private static final long serialVersionUID = 7053589698227135478L;


    public MerchantMenuCategorKey(){
        super();
    }
    public MerchantMenuCategorKey(String merchantCode, String menuCategoryName) {
        this.merchantCode = merchantCode;
        this.menuCategoryName = menuCategoryName;
    }


    /** 外部鍵(部門) */
    @Column(name = "MERCHANT_CODE")
    private String merchantCode;

    /** 外部鍵(部門) */
    @Column(name = "MENU_CATEGORY_NAME")
    private String menuCategoryName;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMenuCategoryName() {
        return menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }
}
