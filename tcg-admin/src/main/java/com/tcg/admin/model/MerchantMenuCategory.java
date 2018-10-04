package com.tcg.admin.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "US_MERCHANT_MENU_CATEGORY")
public class MerchantMenuCategory extends BaseEntity {

    private static final long serialVersionUID = -6461156293509933120L;

    @EmbeddedId
    private MerchantMenuCategorKey key;


    public MerchantMenuCategorKey getKey() {
        return key;
    }

    public void setKey(MerchantMenuCategorKey key) {
        this.key = key;
    }
}
