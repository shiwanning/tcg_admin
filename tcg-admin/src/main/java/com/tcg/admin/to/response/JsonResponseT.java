package com.tcg.admin.to.response;

public class JsonResponseT<T> extends JsonResponse {
    
    private T value;
    
    public JsonResponseT() {
        super(false);
    }
    
    public JsonResponseT(Boolean isSuccess, T value) {
        super(isSuccess);
        this.value = value;
    }
    
    public JsonResponseT(Boolean isSuccess) {
        super(isSuccess);
    }

    public T getValue() {
        return value;
    }

    public JsonResponseT<T> setValue(T value) {
        this.value = value;
        return this;
    }

}
