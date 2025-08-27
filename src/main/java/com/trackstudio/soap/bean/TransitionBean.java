package com.trackstudio.soap.bean;

public class TransitionBean {

    private String id;
    private String startId;
    private String finishId;
    private String mstatusId;

    public TransitionBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public String getFinishId() {
        return finishId;
    }

    public void setFinishId(String finishId) {
        this.finishId = finishId;
    }

    public String getMstatusId() {
        return mstatusId;
    }

    public void setMstatusId(String mstatusId) {
        this.mstatusId = mstatusId;
    }
}
