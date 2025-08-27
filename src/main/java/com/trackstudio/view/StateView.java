package com.trackstudio.view;

import com.trackstudio.secured.SecuredStatusBean;

public abstract class StateView {
    protected SecuredStatusBean status;

    public abstract String getName();
}
