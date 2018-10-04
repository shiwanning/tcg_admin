package com.tcg.admin.to;

/**
 * Created by wen
 * on 2016/11/16.
 * <p/>
 * Capture LoginData
 */
public class LoginDeviceInfoTO {

    private String device;
    private String deviceModel;
    private String operateSystemVersion;
    private String browser;


    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getOperateSystemVersion() {
        return operateSystemVersion;
    }

    public void setOperateSystemVersion(String operateSystemVersion) {
        this.operateSystemVersion = operateSystemVersion;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }
}
