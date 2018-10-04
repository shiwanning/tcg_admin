package com.tcg.admin.to;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chris.h on 2017/1/25.
 */
public class Merchants implements Serializable {

    private static final long serialVersionUID = 8355930389498971266L;

    private List<String> merchantIds;

    private List<String> merchantNames;

    public List<String> getMerchantIds() {
        return merchantIds;
    }

    public void setMerchantIds(List<String> merchantIds) {
        this.merchantIds = merchantIds;
    }

    public List<String> getMerchantNames() {
        return merchantNames;
    }

    public void setMerchantNames(List<String> merchantNames) {
        this.merchantNames = merchantNames;
    }
}
