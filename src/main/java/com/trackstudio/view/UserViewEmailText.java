package com.trackstudio.view;

import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

@Immutable
public class UserViewEmailText extends UserViewText {

    public UserViewEmailText(SecuredUserBean task) {
        super(task);
    }

    public UserView getView(SecuredUserBean t) {
        return new UserViewEmailText(t);
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) {
        return new UDFValueViewEmailText(bean);
    }
}
