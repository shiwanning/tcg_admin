package com.tcg.admin.to.response;

import java.util.Date;

public class AuthInfoTo {
    
    private Boolean active;
    private Date lastValidTime;
    
    public AuthInfoTo(boolean active, Date lastValidTime) {
        this.active = active;
        this.lastValidTime = lastValidTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getLastValidTime() {
        return lastValidTime;
    }

    public void setLastValidTime(Date lastValidTime) {
        this.lastValidTime = lastValidTime;
    }
    
    
    
}
