package com.tcg.admin.to.db;

public class AnnouncementTypeCountTo {
    
    private String announcementType;
    private Integer count;
    
    public AnnouncementTypeCountTo(String announcementType, Integer count) {
        super();
        this.announcementType = announcementType;
        this.count = count;
    }
    
    public String getAnnouncementType() {
        return announcementType;
    }
    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType;
    }
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    
}
