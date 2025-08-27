package com.trackstudio.soap.bean;


public class MstatusHandlerResolutionBean {
    private MstatusBean mstatus;
    private StatusBean nextState;
    private ResolutionBean[] resolutionList;
    private String[] handlerList;
    private String[] handlerGroupList;

    public MstatusHandlerResolutionBean() {

    }

    public String[] getHandlerGroupList() {
        return handlerGroupList;
    }

    public void setHandlerGroupList(String[] handlerGroupList) {
        this.handlerGroupList = handlerGroupList;
    }

    public MstatusBean getMstatus() {
        return mstatus;
    }

    public void setMstatus(MstatusBean mstatus) {
        this.mstatus = mstatus;
    }

    public ResolutionBean[] getResolutionList() {
        return resolutionList;
    }

    public void setResolutionList(ResolutionBean[] resolutionList) {
        this.resolutionList = resolutionList;
    }

    public String[] getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(String[] handlerList) {
        this.handlerList = handlerList;
    }

    public StatusBean getNextState() {
        return nextState;
    }

    public void setNextState(StatusBean nextState) {
        this.nextState = nextState;
    }
}
